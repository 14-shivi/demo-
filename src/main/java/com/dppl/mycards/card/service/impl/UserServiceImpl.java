package com.dppl.mycards.card.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import com.dppl.mycards.card.exception.BadRequestException;
import com.dppl.mycards.card.exception.ErrorResponse;
import com.dppl.mycards.card.exception.InternalServerErrorException;
import com.dppl.mycards.card.exception.NotFoundException;
import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.UserActivityRepository;
import com.dppl.mycards.card.repository.UserDetailsRepository;
import com.dppl.mycards.card.repository.UserRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.repository.model.UserDetails;
import com.dppl.mycards.card.service.UserActivityService;
import com.dppl.mycards.card.service.UserService;
import com.dppl.mycards.card.service.dto.RequestDTO;
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
public class UserServiceImpl implements UserService {

	

	private static final Logger LOGGER = LogManager.getLogger();

	private final UserRepository userRepository;
	private final UserActivityService userActivityService;
	private final UserDetailsRepository userDetailsRepository;
	private final UserActivityRepository userActivityRepository;
	private final JwtProvider jwtProvider;
	private final OtpDetailRepository otpDetailRepository;
	private final RequestParser requestParser = new RequestParser();
	private final EmailUtil emailUtil;

	public UserServiceImpl(UserRepository userRepository,
                           UserActivityService userActivityService,
                           UserDetailsRepository userDetailsRepository,
                           UserActivityRepository userActivityRepository,
                           JwtProvider jwtProvider,
                           OtpDetailRepository otpDetailRepository,
                           EmailUtil emailUtil) {
		this.userRepository = userRepository;
		this.userActivityService = userActivityService;
		this.userDetailsRepository = userDetailsRepository;
		this.userActivityRepository = userActivityRepository;
		this.jwtProvider = jwtProvider;
		this.otpDetailRepository = otpDetailRepository;
		this.emailUtil = emailUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public User createUser(RequestDTO requestDTO) {
	    User user = requestParser.parseUser(requestDTO.getData().getAttributes());
	    user.setPassword(PasswordUtil.encodePassword(user.getPassword()));
	    LOGGER.info("RequestID: {} :: Checking uniqueness of User Credentials.", MDC.get(Keys.REQUEST_ID));
	    if ( !userRepository.findByMobile(user.getMobile()).isEmpty() || 
	    		!userRepository.findByEmail(user.getEmail()).isEmpty())
	    	throw new BadRequestException(ErrorResponse.DUPLICATE_EMAIL_OR_MOBILE);
	    
		String emailOtp = ((Map<String, String>)requestDTO.getData().getAttributes()).get(Keys.EMAIL_OTP);
	    String mobileOtp = ((Map<String, String>)requestDTO.getData().getAttributes()).get(Keys.MOBILE_OTP);
	    
	    String otp = emailOtp != null ? emailOtp : mobileOtp;
	    
	    if (!verifyOtpFromDB(otp, user))
	    	throw new BadRequestException(ErrorResponse.INVALID_OTP);
	    
	    try {
	    	return insertDetailsInDB(requestDTO);
	        
	    } catch (Exception e) {
	    	throw new InternalServerErrorException("database");
	    }
	}
	
	@Transactional
//	@Modifying
	public User insertDetailsInDB(RequestDTO requestDTO) {
		Map<String, String> attributes = requestParser.parseAttributes(requestDTO);
	    
	    User user = createUser(attributes);
	    UserDetails userDetails = createUserDetails(attributes);
	    UserActivity userActivity = createUserActivity(attributes, user);
	    
	    long startTime = System.currentTimeMillis();
	    
	    saveUser(user);
	    saveUserDetails(userDetails);
	    saveUserActivity(userActivity, user);
	    
	    LOGGER.info("{}: {} :: Database operation processing time: {} ms.", 
	    		Keys.REQUEST_ID,
	    		MDC.get(Keys.REQUEST_ID), 
	    		System.currentTimeMillis() - startTime);
	    
	    return user;
	}

	private User createUser(Map<String, String> attributes) {
		
	    User user = requestParser.parseUser(attributes);
	    user.setPassword(PasswordUtil.encodePassword(user.getPassword()));
	    user.setStatus(Keys.USER_STATUS_INACTIVE);
	    user.setCreatedBy("dev-vishwas");
	    user.setCreatedTimestamp(LocalDateTime.now());
	    return user;
	}

	private UserDetails createUserDetails(Map<String, String> attributes) {
	    UserDetails userDetails = requestParser.parseUserDetail(attributes);
	    userDetails.setTermsAcceptedTimestamp(LocalDateTime.now());
	    userDetails.setCreatedBy("dev-vishwas");
	    userDetails.setCreatedTimestamp(LocalDateTime.now());
	    return userDetails;
	}

	private UserActivity createUserActivity(Map<String, String> attributes, User user) {
	    UserActivity userActivity = requestParser.parseUserActivity(attributes);
	    userActivity.setEventType("Account created");
	    userActivity.setEventDetails("New account was created");
	    userActivity.setEventTimestamp(LocalDateTime.now());
	    return userActivity;
	}

	@Transactional
	public void saveUser(User user) {
		LOGGER.info("RequestID: {} :: Inserting User to DB.", MDC.get(Keys.REQUEST_ID));
	    userRepository.save(user);
	}

	@Transactional
	public void saveUserDetails(UserDetails userDetails) {
		LOGGER.info("RequestID: {} :: Inserting UserDetails to DB.", MDC.get(Keys.REQUEST_ID));
		userDetailsRepository.save(userDetails);
	}

	@Transactional
	public void saveUserActivity(UserActivity userActivity, User user) {
		LOGGER.info("RequestID: {} :: Inserting UserActivity to DB.", MDC.get(Keys.REQUEST_ID));
	    userActivity.setUserId(user.getUserId());
	    userActivityRepository.save(userActivity);
	}


	
	public boolean isUserAuthentic(User loginUser) {
		LOGGER.info("RequestID: {} :: Validating user-credentials.", MDC.get(Keys.REQUEST_ID));
	    Optional<User> user = getRegisteredUserFromDB(loginUser);
	    
	    if (user.isEmpty()) {
	        return false;
	    }

	    Argon2 argon2 = Argon2Factory.create();
	    return argon2.verify(
	            user.get().getPassword(),
	            loginUser.getPassword().toCharArray()
	    );
	}

	public Optional<User> getRegisteredUserFromDB(User loginUser) {
	    Optional<User> user;
	    LOGGER.info("RequestID: {} :: Fetching User from DB.", MDC.get(Keys.REQUEST_ID));
	    if (loginUser.getMobile() != null) {
	        user = userRepository.findByMobile(loginUser.getMobile());
	    } else {
	        user = userRepository.findByEmail(loginUser.getEmail());
	    }
	    return user;
	}


	@SuppressWarnings("unchecked")
	public Map<String, Object> loginUser(RequestDTO requestDTO) {
		User loginUser = requestParser.parseUser(requestDTO.getData().getAttributes());
		
		if (isUserAuthentic(loginUser)) {
			LOGGER.info("RequestID: {} :: Logging in.", MDC.get(Keys.REQUEST_ID));
			Optional<User> userOptional = getRegisteredUserFromDB(loginUser);
			if (userOptional.isEmpty())
				throw new NotFoundException(Keys.ENTITY_TYPE_USER, "loginUser");
			
			User user = userOptional.get();
			
			String emailOtp = ((Map<String, String>)requestDTO.getData().getAttributes()).get("emailOtp");
		    String mobileOtp = ((Map<String, String>)requestDTO.getData().getAttributes()).get("mobileOtp");
		    
		    String otp = emailOtp != null ? emailOtp : mobileOtp;

		    if (!verifyOtpFromDB(otp, user))
		    	throw new BadRequestException(ErrorResponse.INVALID_OTP);

			userActivityService.saveUserActivity(
					user.getUserId(), Keys.SIGN_IN_ACTIVITY, 
					"User signed in", 
					requestDTO);
			
			String token = jwtProvider.generateToken(user);

			return Map.of("token", token, Keys.USER_ID, user.getUserId());
		}
		
		// TODO: Logic to toggle user's session=true could be added here if needed.
		throw new BadRequestException(ErrorResponse.USER_NOT_REGISTERED);
	}

	public Map<String, Object> getUserById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);	
		if (userOptional.isEmpty()) {
	    	throw new NotFoundException(Keys.ENTITY_TYPE_USER, "getUserById");
		}
		
		Optional<UserDetails> userDetailsOptional = userDetailsRepository.findById(id);
		if (userDetailsOptional.isEmpty()) {
	    	throw new NotFoundException(Keys.ENTITY_TYPE_USER_DETAILS, "getUserById");
		}
		
		User user = userOptional.get();
		UserDetails userDetails = userDetailsOptional.get();

		Map<String, Object> map = new HashMap<>();
		map.put(Keys.USER_ID, user.getUserId());
		map.put(Keys.USER_DETAILS_FIRST_NAME, userDetails.getFirstName());
		map.put(Keys.USER_DETAILS_LAST_NAME, userDetails.getLastName());
		map.put(Keys.USER_EMAIL, user.getEmail());
		map.put(Keys.USER_MOBILE, user.getMobile());
		map.put(Keys.USER_DETAILS_SALUTATION, userDetails.getSalutation());
		map.put(Keys.USER_DETAILS_DOB, userDetails.getDob());

		return map;
	}

	@Transactional
	public User updateUser(RequestDTO requestDTO) {
		
		User updatedUserInfo = requestParser.parseUser(requestDTO.getData().getAttributes());		
		Optional<User> existingUserOptional = getRegisteredUserFromDB(updatedUserInfo);
		if (existingUserOptional.isEmpty()) {
			throw new NotFoundException(Keys.ENTITY_TYPE_USER, "updateUser");
		}
		User existingUser = existingUserOptional.get();
		existingUser.setEmail(updatedUserInfo.getEmail());
		existingUser.setMobile(updatedUserInfo.getMobile());
		existingUser.setUpdatedBy(updatedUserInfo.getUpdatedBy());
		existingUser.setPassword(PasswordUtil.encodePassword(updatedUserInfo.getPassword()));
		existingUser.setUpdatedBy("dev-vishwas");
		existingUser.setUpdatedTimestamp(LocalDateTime.now());
		
		Map<String, String> attributes = requestParser.parseAttributes(requestDTO);
		
		UserActivity userActivity = UserActivity.builder()
				.eventType("Update profile")
				.eventDetails("Made profile changes")
				.eventTimestamp(LocalDateTime.now())
				.ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
				.build();
		
		long startTime = System.currentTimeMillis();
		LOGGER.info("RequestID: {} :: Saving updated User to DB.", MDC.get(Keys.REQUEST_ID));
		User user = userRepository.save(existingUser);

		userActivity.setUserId(user.getUserId());
		
		LOGGER.info("RequestID: {} :: Saving UserActivity.", MDC.get(Keys.REQUEST_ID));
		saveUserActivity(userActivity, user);
		
		LOGGER.info("{}: {} :: Database operation processing time: {} ms.", 
	    		Keys.REQUEST_ID,
	    		MDC.get(Keys.REQUEST_ID), 
	    		System.currentTimeMillis() - startTime);

		return user;
	}

	public void deleteUser(Long id) {
		
		try {
			userRepository.deleteById(id);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("ID is null");
		}
	}

	public String generateOtp(RequestDTO requestDTO, OtpContext context) {
	    User user = requestParser.parseUser(requestDTO.getData().getAttributes());

	    if (context == OtpContext.PASSWORD_RESET) {
	        Optional<User> registeredUserOptional = getRegisteredUserFromDB(user);
	        if (registeredUserOptional.isEmpty()) {
	            throw new NotFoundException(Keys.ENTITY_TYPE_USER, "generateOtp");
	        }
	        user = registeredUserOptional.get();
	    }

	    LOGGER.info("RequestID: {} :: Generating OTP.", MDC.get(Keys.REQUEST_ID));

	    String otp = generateRandomOtp();
	    updateOrCreateOtpDetail(user, otp);
	    
	    String eventType = switch (context) {
						    	case REGISTRATION -> "Registration";
						    	case PASSWORD_RESET -> "Password reset";
						    	case LOGIN -> "Login";
						    };
	    
	    logUserActivity(requestDTO, eventType, "OTP was generated");

	    // Shall we move this to a new therad?
//	    sendOtpEmail(user.getEmail(), otp);
	    final String email = user.getEmail();
	    CompletableFuture.runAsync(() -> sendOtpEmail(email, otp));

	    return otp;
	}

	private String generateRandomOtp() {
	    return Integer.toString(100000 + new Random().nextInt(900000));
	}

	private void updateOrCreateOtpDetail(User user, String otp) {
	    Optional<OtpDetail> otpDetailOptional = Optional.empty();

	    if (user.getUserId() != null) {
	        otpDetailOptional = otpDetailRepository.findByUserId(user.getUserId());
	    }
	    if (otpDetailOptional.isEmpty() && user.getEmail() != null) {
	        otpDetailOptional = otpDetailRepository.findByEmail(user.getEmail());
	    }
	    if (otpDetailOptional.isEmpty() && user.getMobile() != null) {
	        otpDetailOptional = otpDetailRepository.findByMobile(user.getMobile());
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
	                .userId(user.getUserId())
	                .emailOtp(otp)
	                .mobileOtp(otp)
	                .createdTimestamp(LocalDateTime.now())
	                .expiryTimestamp(LocalDateTime.now().plusMinutes(5))
	                .email(user.getEmail())
	                .mobile(user.getMobile())
	                .createdBy("vishwas-dev")
	                .build();
	    }
	    otpDetailRepository.save(otpDetail);
	}


	private void logUserActivity(RequestDTO requestDTO, String eventType, String eventDetails) {
	    Map<String, String> attributes = requestParser.parseAttributes(requestDTO);

	    UserActivity userActivity = UserActivity.builder()
	            .eventType(eventType)
	            .eventDetails(eventDetails)
	            .eventTimestamp(LocalDateTime.now())
	            .ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
	            .build();

	    long startTime = System.currentTimeMillis();
	    userActivityRepository.save(userActivity);
	    LOGGER.info("{}: {} :: Database operation processing time: {} ms.",
	            Keys.REQUEST_ID,
	            MDC.get(Keys.REQUEST_ID),
	            System.currentTimeMillis() - startTime);
	}

	private void sendOtpEmail(String email, String otp) {
	    long startTime = System.currentTimeMillis();
	    emailUtil.sendOtp(email, otp);
	    LOGGER.info("{}: {} :: OTP sending operation processing time: {} ms.",
	            Keys.REQUEST_ID,
	            MDC.get(Keys.REQUEST_ID),
	            System.currentTimeMillis() - startTime);
	}

	
	// returns true for successfull verification
	public boolean verifyOtp(RequestDTO requestDTO) {

		Map<String, String> attributes = requestParser.parseAttributes(requestDTO);
		User user = requestParser.parseUser(attributes);
		Optional<User> optionalUser = getRegisteredUserFromDB(user);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException(Keys.ENTITY_TYPE_USER, "verifyOtp");
		}

		String otp = attributes.get(Keys.MOBILE_OTP) != null ? attributes.get(Keys.MOBILE_OTP) : attributes.get(Keys.EMAIL_OTP);

		LOGGER.info("RequestID: {} :: Verifying OTP.", MDC.get(Keys.REQUEST_ID));
		
		UserActivity userActivity = UserActivity.builder()
				.eventType(Keys.OTP_VERIFIED_ACTIVITY)
				.eventDetails("OTP verification request was made")
				.eventTimestamp(LocalDateTime.now())
				.ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
				.build();
		
		long startTime = System.currentTimeMillis();
		userActivityRepository.save(userActivity);
		boolean isVerified = verifyOtpFromDB(otp, optionalUser.get());
		LOGGER.info("RequestID: {} :: Database operation processing time: {} ms.", 
	    		MDC.get(Keys.REQUEST_ID), System.currentTimeMillis() - startTime);
		return isVerified;
	}
	
	// returns true for successfull verification
	public boolean verifyOtpFromDB(String otpFromUser, User user) {
		LOGGER.info("RequestID: {} :: Fetching and verifying OTP from databse", MDC.get(Keys.REQUEST_ID));
		
		Optional<OtpDetail> otpDetailOptional = null;
		
		if (user.getEmail() != null)
			otpDetailOptional = otpDetailRepository.findByEmail(user.getEmail());
		else
			otpDetailOptional = otpDetailRepository.findByMobile(user.getMobile());

		if (otpDetailOptional.isEmpty())
			throw new BadRequestException(ErrorResponse.INVALID_OTP);
		OtpDetail otpDetail = otpDetailOptional.get();
		if (otpDetail.getExpiryTimestamp().isBefore(LocalDateTime.now()))
			throw new BadRequestException(ErrorResponse.OTP_EXPIRED);
		
		String otpFromDatabase = otpDetail.getEmailOtp() != null ? otpDetail.getEmailOtp(): otpDetail.getMobileOtp();
				
		return otpFromUser.equals(otpFromDatabase);
	}

	public String forgotPassword(RequestDTO requestDTO) {
		
		Map<String, String> attributes = requestParser.parseAttributes(requestDTO);
		User user = requestParser.parseUser(attributes);
		
		Optional<User> userOptional = getRegisteredUserFromDB(user);
		if (userOptional.isEmpty()) {
			throw new NotFoundException(Keys.ENTITY_TYPE_USER, "forgotPassword");
		}
		
		user = userOptional.get();
		
		String otp = attributes.get("mobileOtp") != null ? attributes.get("mobileOtp") : attributes.get("emailOtp");
		
		if (!verifyOtpFromDB(otp, user)) {
			throw new BadRequestException(ErrorResponse.INVALID_OTP);
		}
		
		UserActivity userActivity = UserActivity.builder()
				.eventType("Forgot password")
				.eventDetails("Password change request was made")
				.eventTimestamp(LocalDateTime.now())
				.ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
				.build();
		
		long startTime = System.currentTimeMillis();
		userActivityRepository.save(userActivity);
		LOGGER.info("RequestID: {} :: Database operation processing time: {} ms.", 
		    		MDC.get(Keys.REQUEST_ID), System.currentTimeMillis() - startTime);
		
		return new JwtProvider().generateToken(user);
	}
	
}
