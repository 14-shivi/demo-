package com.dppl.mycards.card.service;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.PartnerActivityRepository;
import com.dppl.mycards.card.repository.PartnerRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.impl.PartnerServiceImpl;
import com.dppl.mycards.card.utility.JwtProvider;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.PasswordUtil;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerServiceImplTest {

	private OtpDetail otpDetail;
	
	private Argon2 argon2;
	
	@MockBean
	private PartnerRepository partnerRepository;
	
	@MockBean
	private PartnerActivityRepository partnerActivityRepository;
	
	@MockBean
	private JwtProvider jwtProvider;
	
	@MockBean
	private OtpDetailRepository otpDetailRepository;
	
	@InjectMocks
	private PartnerServiceImpl partnerService;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otpDetail = OtpDetail.builder()
        		.emailOtp("123456")
        		.mobileOtp("123456")
        		.build();
        argon2 = Argon2Factory.create();
    }
	
	@Test
	void loginPartnerTest() {
		Map<String, String> requestAttributes = generatePartnerRequestAttributes();
		Partner partner = generatePartnerUsingAttributes(requestAttributes);
		partner.setId(1L);
		partner.setPassword(PasswordUtil.encodePassword(partner.getPassword()));
		
		when(partnerRepository.findByMobile(any())).thenReturn(Optional.of(partner));
        when(partnerActivityRepository.save(any())).thenReturn(null);
        String token = "xyzabcdToken";
		when(jwtProvider.generateToken(any(Partner.class))).thenReturn(token);
		otpDetail = OtpDetail.builder()
				.emailOtp(requestAttributes.get(Keys.EMAIL_OTP))
				.mobileOtp(requestAttributes.get(Keys.MOBILE_OTP))
				.expiryTimestamp(LocalDateTime.now().plusMinutes(4))
				.build();
		when(otpDetailRepository.findByMobile(any())).thenReturn(Optional.of(otpDetail));
		when(otpDetailRepository.findByEmail(any())).thenReturn(Optional.of(otpDetail));

        
        Map<String, Object> loginPartner = partnerService.loginPartner(requestAttributes);
        
        assertNotNull(loginPartner);
	}
	
	@Test
	void resetPasswordTest() {
		Map<String, String> requestAttributes = generatePartnerRequestAttributes();
		Partner partner = generatePartnerUsingAttributes(requestAttributes);

		when(partnerRepository.findByMobile(any())).thenReturn(Optional.of(partner));
        when(partnerActivityRepository.save(any())).thenReturn(null);
        otpDetail = OtpDetail.builder()
				.emailOtp(requestAttributes.get(Keys.EMAIL_OTP))
				.mobileOtp(requestAttributes.get(Keys.MOBILE_OTP))
				.expiryTimestamp(LocalDateTime.now().plusMinutes(4))
				.build();
		when(otpDetailRepository.findByMobile(any())).thenReturn(Optional.of(otpDetail));
		when(otpDetailRepository.findByEmail(any())).thenReturn(Optional.of(otpDetail));
        
		String tokenResponse = partnerService.resetPassword(requestAttributes);
		
		assertNotNull(tokenResponse);
	}
	
	@Test
	void updateProfileTest() {
		Map<String, String> requestAttributes = generatePartnerRequestAttributes();
		Partner partner = generatePartnerUsingAttributes(requestAttributes);
		
		when(partnerRepository.findByMobile(anyString())).thenReturn(Optional.of(partner));
        when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(null);
		when(partnerRepository.save(any(Partner.class))).thenReturn(partner);
		
		Partner partnerResponse = partnerService.updateProfile(requestAttributes);
		
		assertNotNull(partnerResponse);		
	}
	
	@Test
	void signUpTest() {
		Map<String, String> requestAttributes = generatePartnerRequestAttributes();
		Partner partner = generatePartnerUsingAttributes(requestAttributes);
		
		when(partnerRepository.findByMobile(anyString())).thenReturn(Optional.empty());
		when(partnerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(null);
		partner.setId(1L);
		when(partnerRepository.save(any(Partner.class))).thenReturn(partner);
		otpDetail = OtpDetail.builder()
				.emailOtp(requestAttributes.get(Keys.EMAIL_OTP))
				.mobileOtp(requestAttributes.get(Keys.MOBILE_OTP))
				.expiryTimestamp(LocalDateTime.now().plusMinutes(4))
				.build();
		when(otpDetailRepository.findByMobile(any())).thenReturn(Optional.of(otpDetail));
		when(otpDetailRepository.findByEmail(any())).thenReturn(Optional.of(otpDetail));
		
		Partner partnerResponse = partnerService.signUp(requestAttributes);
		
		assertNotNull(partnerResponse);	
		
	}
	
	@Test
	void generateOtpTest() {
	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
	    Partner partner = generatePartnerUsingAttributes(requestAttributes);
	    
	    when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(null);
	    when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

	    when(otpDetailRepository.findByEmail(anyString())).thenReturn(Optional.empty());
	    
	    String otp = partnerService.generateOtp(requestAttributes);
	    
	    assertNotNull(otp);
	    assertEquals(6, otp.length());
	    System.out.println("Generated OTP: " + otp);
	}
	
	@Test
	void verifyOtpTest() {
	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
	    Partner partner = generatePartnerUsingAttributes(requestAttributes);

	    when(partnerRepository.findById(partner.getId())).thenReturn(Optional.of(partner));
	    when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(null);
	    otpDetail = OtpDetail.builder()
				.emailOtp(requestAttributes.get(Keys.EMAIL_OTP))
				.mobileOtp(requestAttributes.get(Keys.MOBILE_OTP))
				.expiryTimestamp(LocalDateTime.now().plusMinutes(4))
				.build();
		when(otpDetailRepository.findByMobile(any())).thenReturn(Optional.of(otpDetail));
		when(otpDetailRepository.findByEmail(any())).thenReturn(Optional.of(otpDetail));

	    boolean isVerified = partnerService.verifyOtp(requestAttributes);

	    assertTrue(isVerified);
	}
	
	@Test
	void saveActivityTest() {
	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
	    PartnerActivity activity = new PartnerActivity();
	    activity.setActivityId(1L);

	    when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(activity);

	    PartnerActivity savedActivity = partnerService.saveActivity(requestAttributes);

	    assertNotNull(savedActivity);
	}
	
	@Test
	void getActivityForSpecificPartnerTest() {
	    String partnerIdString = "1";
	    Map<String, String> requestAttributes = generatePartnerActivityRequestAttributes();
	    List<PartnerActivity> activities = List.of(
	    		new PartnerActivity(null, 1L, partnerIdString, partnerIdString, null, partnerIdString), 
	    		new PartnerActivity(null, 1L, partnerIdString, partnerIdString, null, partnerIdString));
	    
	    when(partnerRepository.existsById(1L)).thenReturn(true);
	    when(partnerActivityRepository.findByCriteria(
	            any(LocalDateTime.class), 
	            any(LocalDateTime.class), 
	            anyString(), 
	            eq(1L)
	    )).thenReturn(activities);

	    List<PartnerActivity> result = partnerService.getActivityForSpecificPartner(partnerIdString, requestAttributes);

	    assertNotNull(result);
	    assertEquals(2, result.size());
	}
	
	@Test
	void getAllActivitiesBasedOnEventTypeTest() {
	    Map<String, String> requestAttributes = Map.of(Keys.PARTNER_ACTIVITY_EVENT_TYPE, Keys.SIGN_IN_ACTIVITY);
	    List<PartnerActivity> activities = List.of(new PartnerActivity(), new PartnerActivity());

	    when(partnerActivityRepository.findByEventType(Keys.SIGN_IN_ACTIVITY)).thenReturn(activities);

	    List<PartnerActivity> result = partnerService.getAllActivitiesBasedOnEventType(requestAttributes);

	    assertNotNull(result);
	    assertEquals(2, result.size());
	    verify(partnerActivityRepository).findByEventType(Keys.SIGN_IN_ACTIVITY );
	}

	@Test
	void generateOtpWithContextTest() {
	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
	    Partner partner = generatePartnerUsingAttributes(requestAttributes);

	    when(partnerRepository.findById(partner.getId())).thenReturn(Optional.of(partner));
	    when(partnerActivityRepository.save(any(PartnerActivity.class))).thenReturn(null);

	    String generatedOtp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);

	    assertNotNull(generatedOtp);
	    assertEquals(6, generatedOtp.length());
	}

}
