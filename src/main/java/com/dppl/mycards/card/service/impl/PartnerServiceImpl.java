package com.dppl.mycards.card.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.dppl.mycards.card.exception.BadRequestException;
import com.dppl.mycards.card.exception.ErrorResponse;
import com.dppl.mycards.card.exception.InternalServerErrorException;
import com.dppl.mycards.card.exception.NotFoundException;
import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.PartnerActivityRepository;
import com.dppl.mycards.card.repository.PartnerRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.service.PartnerService;
import com.dppl.mycards.card.utility.EmailUtil;
import com.dppl.mycards.card.utility.JwtProvider;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.PasswordUtil;
import com.dppl.mycards.card.utility.RequestParser;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.transaction.Transactional;

@Service
public class PartnerServiceImpl implements PartnerService {

    private static final Logger LOGGER = LogManager.getLogger();
    
    private final RequestParser requestParser = new RequestParser();

    @Autowired
	private JwtProvider jwtProvider;
    
    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
	private OtpDetailRepository otpDetailRepository;

    @Autowired
	private PartnerActivityRepository partnerActivityRepository;

    @Autowired
	private EmailUtil emailUtil;

	@Override
	public Map<String, Object> loginPartner(Map<String, String> requestAttributes) {
		LOGGER.info("RequestId: {} :: loginPartner()", MDC.get(Keys.REQUEST_ID));
		Partner loginPartner = requestParser.parsePartner(requestAttributes);

		if (isPartnerAuthentic(loginPartner)) {
			LOGGER.info("RequestID: {} :: Logging in.", MDC.get(Keys.REQUEST_ID));
			Optional<Partner> partnerOptional = getRegisteredPartnerFromDB(loginPartner);
			if (partnerOptional.isEmpty())
				throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "loginPartner");
			
			Partner partner = partnerOptional.get();
			
			String emailOtp = (requestAttributes.get("emailOtp"));
		    String mobileOtp = (requestAttributes.get("mobileOtp"));
		    
		    String otp = emailOtp != null ? emailOtp : mobileOtp;

		    if (!verifyOtpFromDB(otp, partner))
		    	throw new BadRequestException(ErrorResponse.INVALID_OTP);

			savePartnerActivity(partner.getId(), Keys.SIGN_IN_ACTIVITY, requestAttributes);
			
			String token = jwtProvider.generateToken(partner);
			
			// TODO: Logic to toggle user's session=true could be added here if needed.
			return Map.of("token", token, Keys.USER_ID, partner.getId());
		}
		
		throw new BadRequestException(ErrorResponse.USER_NOT_REGISTERED);
	}

	private PartnerActivity savePartnerActivity(Long id, String activityType, Map<String, String> requestAttributes) {
		LOGGER.info("RequestId: {} :: Saving Activity", MDC.get(Keys.REQUEST_ID));
		
		PartnerActivity partnerActivity = requestParser.parsePartnerActivity(requestAttributes);
		partnerActivity.setPartnerId(id);
		partnerActivity.setEventType(activityType);
		return partnerActivityRepository.save(partnerActivity);
		
	}

	private boolean verifyOtpFromDB(String otpFromPartner, Partner partner) {
		LOGGER.info("RequestID: {} :: Fetching and verifying OTP from databse", MDC.get(Keys.REQUEST_ID));
		
		Optional<OtpDetail> otpDetailOptional;

		if (partner.getEmail() != null)
			otpDetailOptional = otpDetailRepository.findByEmail(partner.getEmail());
		else
			otpDetailOptional = otpDetailRepository.findByMobile(partner.getMobile());

		if (otpDetailOptional.isEmpty())
			throw new BadRequestException(ErrorResponse.INVALID_OTP);
		OtpDetail otpDetail = otpDetailOptional.get();
		if (otpDetail.getExpiryTimestamp().isBefore(LocalDateTime.now()))
			throw new BadRequestException(ErrorResponse.OTP_EXPIRED);
		
		String otpFromDatabase = otpDetail.getEmailOtp() != null ? otpDetail.getEmailOtp(): otpDetail.getMobileOtp();

		return otpFromPartner.equals(otpFromDatabase);
	}
	

	private Optional<Partner> getRegisteredPartnerFromDB(Partner loginPartner) {
		Optional<Partner> partner;
	    LOGGER.info("RequestID: {} :: Fetching Partner from DB.", MDC.get(Keys.REQUEST_ID));
	    if (loginPartner.getMobile() != null) {
	        partner = partnerRepository.findByMobile(loginPartner.getMobile());
	    } else {
	        partner = partnerRepository.findByEmail(loginPartner.getEmail());
	    }
	    return partner;
	}

	private boolean isPartnerAuthentic(Partner loginPartner) {
		LOGGER.info("RequestID: {} :: Validating partner-credentials.", MDC.get(Keys.REQUEST_ID));
		Optional<Partner> partner = getRegisteredPartnerFromDB(loginPartner);
	    if (partner.isEmpty()) {
	        return false;
	    }

	    Argon2 argon2 = Argon2Factory.create();
	    return argon2.verify(
	    		partner.get().getPassword(),
	    		loginPartner.getPassword().toCharArray()
	    );
	}

	@Override
	public String resetPassword(Map<String, String> attributes) {
		Partner partner = requestParser.parsePartner(attributes);

		Optional<Partner> partnerOptional = getRegisteredPartnerFromDB(partner);
		if (partnerOptional.isEmpty()) {
		    throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "forgotPassword");
		}

		partner = partnerOptional.get();

		String otp = attributes.get("mobileOtp") != null ? attributes.get("mobileOtp") : attributes.get("emailOtp");

		if (!verifyOtpFromDB(otp, partner)) {
		    throw new BadRequestException(ErrorResponse.INVALID_OTP);
		}

		PartnerActivity partnerActivity = PartnerActivity.builder()
		        .eventType(Keys.PASSWORD_RESET_ACTIVITY)
		        .eventDetails("Password change request was made")
		        .eventTimestamp(LocalDateTime.now())
		        .ipAddress(attributes.get(Keys.PARTNER_IP_ADDRESS))
		        .build();

		long startTime = System.currentTimeMillis();
		partnerActivityRepository.save(partnerActivity);
		LOGGER.info("RequestID: {} :: Database operation processing time: {} ms.", 
		            MDC.get(Keys.REQUEST_ID), System.currentTimeMillis() - startTime);

		return new JwtProvider().generateToken(partner);

	}

	@Override
	public Partner updateProfile(Map<String, String> requestAttributes) {
		Partner updatedPartnerInfo = requestParser.parsePartner(requestAttributes);
		Optional<Partner> existingPartnerOptional = getRegisteredPartnerFromDB(updatedPartnerInfo);
		if (existingPartnerOptional.isEmpty()) {
		    throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "updatePartner");
		}

		Partner existingPartner = existingPartnerOptional.get();
		existingPartner.setEmail(updatedPartnerInfo.getEmail());
		existingPartner.setMobile(updatedPartnerInfo.getMobile());
		existingPartner.setPassword(PasswordUtil.encodePassword(updatedPartnerInfo.getPassword()));
		existingPartner.setIpAddress(updatedPartnerInfo.getIpAddress());
		existingPartner.setCompanyName(updatedPartnerInfo.getCompanyName());
		existingPartner.setCompanyAddress(updatedPartnerInfo.getCompanyAddress());
		existingPartner.setCin(updatedPartnerInfo.getCin());
		existingPartner.setGstNo(updatedPartnerInfo.getGstNo());
		existingPartner.setLicense(updatedPartnerInfo.getLicense());
		existingPartner.setDateOfIncorporation(updatedPartnerInfo.getDateOfIncorporation());

		long startTime = System.currentTimeMillis();
		LOGGER.info("RequestID: {} :: Saving updated Partner to DB.", MDC.get(Keys.REQUEST_ID));
		Partner partner = partnerRepository.save(existingPartner);

		LOGGER.info("RequestID: {} :: Saving PartnerActivity.", MDC.get(Keys.REQUEST_ID));
		savePartnerActivity(partner.getId(), Keys.UPDATE_PROFILE_ACTIVITY, requestAttributes);

		LOGGER.info("{}: {} :: Database operation processing time: {} ms.",
		        Keys.REQUEST_ID,
		        MDC.get(Keys.REQUEST_ID),
		        System.currentTimeMillis() - startTime);

		return partner;

	}

	@Transactional
	public Partner signUp(Map<String, String> requestAttributes) {
	    Partner partner = new RequestParser().parsePartner(requestAttributes);
	    partner.setPassword(PasswordUtil.encodePassword(partner.getPassword()));
	    LOGGER.info("RequestID: {} :: Checking uniqueness of Partner Credentials.", MDC.get(Keys.REQUEST_ID));
	    
	    if (!partnerRepository.findByMobile(partner.getMobile()).isEmpty() ||
	        !partnerRepository.findByEmail(partner.getEmail()).isEmpty())
	        throw new BadRequestException(ErrorResponse.DUPLICATE_EMAIL_OR_MOBILE);
	    
	    String emailOtp = requestAttributes.get(Keys.EMAIL_OTP);
	    String mobileOtp = requestAttributes.get(Keys.MOBILE_OTP);
	    
	    String otp = emailOtp != null ? emailOtp : mobileOtp;
	    
	    if (!verifyOtpFromDB(otp, partner))
	        throw new BadRequestException(ErrorResponse.INVALID_OTP);
	    
	    try {
	        return insertPartnerDetailsInDB(requestAttributes, Keys.ACCOUNT_CREATED_ACTIVITY);   
	    } catch (Exception e) {
	        throw new InternalServerErrorException(e.getMessage());
	    }
	}
	
	@Transactional
	public Partner insertPartnerDetailsInDB(Map<String, String> requestAttributes, String eventType) {
	    Partner partner = requestParser.parsePartner(requestAttributes);
	    partner.setPassword(PasswordUtil.encodePassword(partner.getPassword()));
	    
	    long startTime = System.currentTimeMillis();
	    partner = partnerRepository.save(partner);
	    savePartnerActivity(partner.getId(), eventType, requestAttributes);
	    
	    LOGGER.info("{}: {} :: Database operation processing time: {} ms.",
	            Keys.REQUEST_ID,
	            MDC.get(Keys.REQUEST_ID),
	            System.currentTimeMillis() - startTime);
	    
	    return partner;
	}

	@Override
	public String generateOtp(Map<String, String> requestAttributes) {
		Partner partner = requestParser.parsePartner(requestAttributes);
		LOGGER.info("RequestID: {} :: Generating OTP.", MDC.get(Keys.REQUEST_ID));

		String otp = Integer.toString(100000 + new Random().nextInt(900000));
		updateOrCreateOtpDetail(partner, otp);

		savePartnerActivity(partner.getId(), Keys.OTP_GENERATED_ACTIVITY, requestAttributes);

		// Send OTP email asynchronously
//		final String email = partner.getEmail();
//		CompletableFuture.runAsync(() -> sendOtpEmail(email, otp));

		return otp;

	}

	private void updateOrCreateOtpDetail(Partner partner, String otp) {
		Optional<OtpDetail> otpDetailOptional = Optional.empty();

		if (partner.getId() != null) {
		    otpDetailOptional = otpDetailRepository.findByPartnerId(partner.getId());
		}
		if (otpDetailOptional.isEmpty() && partner.getEmail() != null) {
		    otpDetailOptional = otpDetailRepository.findByEmail(partner.getEmail());
		}
		if (otpDetailOptional.isEmpty() && partner.getMobile() != null) {
		    otpDetailOptional = otpDetailRepository.findByMobile(partner.getMobile());
		}

		OtpDetail otpDetail;
		if (otpDetailOptional.isPresent()) {
		    otpDetail = otpDetailOptional.get();
		    otpDetail.setEmailOtp(otp);
		    otpDetail.setMobileOtp(otp);
		    otpDetail.setUpdatedBy("vishwas-dev");
		    otpDetail.setUpdatedTimestamp(LocalDateTime.now());
		    otpDetail.setExpiryTimestamp(LocalDateTime.now().plusMinutes(5));
		} else {
		    otpDetail = OtpDetail.builder()
		            .partnerId(partner.getId())
		            .emailOtp(otp)
		            .mobileOtp(otp)
		            .createdTimestamp(LocalDateTime.now())
		            .expiryTimestamp(LocalDateTime.now().plusMinutes(5))
		            .email(partner.getEmail())
		            .mobile(partner.getMobile())
		            .createdBy("vishwas-dev")
		            .build();
		}
		otpDetailRepository.save(otpDetail);		
	}

	@Override
	public boolean verifyOtp(Map<String, String> requestAttributes) {
		Partner partner = requestParser.parsePartner(requestAttributes);
		Optional<Partner> optionalPartner = partnerRepository.findById(partner.getId());
		if (optionalPartner.isEmpty()) {
		    throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "verifyOtp");
		}

		String otp = requestAttributes.get(Keys.MOBILE_OTP) != null ? 
				requestAttributes.get(Keys.MOBILE_OTP) : requestAttributes.get(Keys.EMAIL_OTP);

		LOGGER.info("RequestID: {} :: Verifying OTP.", MDC.get(Keys.REQUEST_ID));

		PartnerActivity partnerActivity = PartnerActivity.builder()
		        .eventType(Keys.OTP_VERIFIED_ACTIVITY)
		        .eventDetails("OTP verification request was made")
		        .eventTimestamp(LocalDateTime.now())
		        .ipAddress(requestAttributes.get(Keys.PARTNER_IP_ADDRESS))
		        .build();

		long startTime = System.currentTimeMillis();
		partnerActivityRepository.save(partnerActivity);

		boolean isVerified = verifyOtpFromDB(otp, optionalPartner.get());

		LOGGER.info("RequestID: {} :: Database operation processing time: {} ms.", 
		        MDC.get(Keys.REQUEST_ID), System.currentTimeMillis() - startTime);

		return isVerified;

	}

	@Override
	public PartnerActivity saveActivity(Map<String, String> requestAttributes) {
		LOGGER.info("RequestID: {} :: Saving partner activity.", MDC.get(Keys.REQUEST_ID));
		PartnerActivity activity = requestParser.parsePartnerActivity(requestAttributes);

		return partnerActivityRepository.save(activity);
	}

	@Override
	public List<PartnerActivity> getActivityForSpecificPartner(String partnerIdString,
			Map<String, String> requestAttributes) {
		LOGGER.info("RequestId: {} :: Finding user activity from DB for a specific user", MDC.get(Keys.REQUEST_ID));
		
		// Checking if partner exists
		Long partnerId = Long.parseLong(partnerIdString);
		if (!partnerRepository.existsById(partnerId))
			throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "getActivityForSpecificPartner");
	    
	    String startDateString = requestAttributes.get("startDate");
	    LocalDateTime startDateTime = startDateString != null 
	        ? LocalDate.parse(startDateString).atStartOfDay() 
	        : LocalDateTime.of(1970, 1, 1, 0, 0);

	    String endDateString = requestAttributes.get("endDate");
	    LocalDateTime endDateTime = endDateString != null 
	        ? LocalDate.parse(endDateString).atTime(23, 59, 59) 
	        : LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

	    String eventType = requestAttributes.get("eventType");
	    String email = requestAttributes.get("email");
	    String mobile = requestAttributes.get("mobile");

	    return partnerActivityRepository.findByCriteria(startDateTime, endDateTime, eventType, partnerId);   
	}

	@Override
	public List<PartnerActivity> getAllActivitiesBasedOnEventType(Map<String, String> requestAttributes) {
		LOGGER.info("RequestId: {} :: Finding activity from DB based on event", MDC.get(Keys.REQUEST_ID));
		return partnerActivityRepository.findByEventType(requestAttributes.get(Keys.PARTNER_ACTIVITY_EVENT_TYPE));
	}

	@Override
	public String generateOtp(Map<String, String> requestAttributes, OtpContext context) {
		Partner partner = requestParser.parsePartner(requestAttributes);

		if (context == OtpContext.PASSWORD_RESET) {
		    Optional<Partner> registeredPartnerOptional = getRegisteredPartnerFromDB(partner);
		    if (registeredPartnerOptional.isEmpty()) {
		        throw new NotFoundException(Keys.ENTITY_TYPE_PARTNER, "generateOtp");
		    }
		    partner = registeredPartnerOptional.get();
		}

		LOGGER.info("RequestID: {} :: Generating OTP.", MDC.get(Keys.REQUEST_ID));
		String otp = Integer.toString(100000 + new Random().nextInt(900000));
		
		updateOrCreateOtpDetail(partner, otp);

		String eventType = switch (context) {
		                    case REGISTRATION -> Keys.ACCOUNT_CREATED_ACTIVITY;
		                    case PASSWORD_RESET -> Keys.PASSWORD_RESET_ACTIVITY;
		                    case LOGIN -> Keys.SIGN_IN_ACTIVITY;
		                };

		savePartnerActivity(partner.getId(), eventType, requestAttributes);

		// Sending OTP email asynchronously
//		final String email = partner.getEmail();
//		CompletableFuture.runAsync(() -> sendOtpEmail(email, otp));

		return otp;
	}

	private void sendOtpEmail(String email, String otp) {
		long startTime = System.currentTimeMillis();
	    emailUtil.sendOtp(email, otp);
	    LOGGER.info("{}: {} :: OTP sending operation processing time: {} ms.",
	            Keys.REQUEST_ID,
	            MDC.get(Keys.REQUEST_ID),
	            System.currentTimeMillis() - startTime);
	}

    
}

