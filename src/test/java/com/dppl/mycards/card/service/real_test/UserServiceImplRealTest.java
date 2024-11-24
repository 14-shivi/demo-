package com.dppl.mycards.card.service.real_test;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dppl.mycards.card.exception.BadRequestException;
import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.UserRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.impl.UserServiceImpl;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;

@SpringBootTest
class UserServiceImplRealTest {
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OtpDetailRepository otpDetailRepository;
	
	@Test
	void createUserTest() {
    	Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);

    	String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
	    User createdUser = userService.createUser(requestDTO);
	    
	    assertNotNull(createdUser);
	    assertEquals(attributes.get(Keys.USER_EMAIL), createdUser.getEmail());
	    assertEquals(attributes.get(Keys.USER_MOBILE), createdUser.getMobile());
	    assertNotNull(userRepository.findById(createdUser.getUserId()));
	}
	
	@Test
	void createUserTest_ThrowsDuplicateUserException() {
    	Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
    	
    	// create
	    userService.createUser(requestDTO);
	    
	    otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
    	
    	// create again with same credentials
	    assertThrows(BadRequestException.class, () -> userService.createUser(requestDTO));
	}
	
	@Test
	void loginUserTest() {			
    	Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
    	
	    // Create user first
	    userService.createUser(requestDTO);
	    
	    // Login
	    otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
    	Map<String, Object> loginResponseAttributes = userService.loginUser(requestDTO);
    	 
    	String token = (String) loginResponseAttributes.get("token");

	    assertNotNull(token);
	}

	@Test
	void loginUserTest_ThrowsInvalidCredentialsExceptions() {
		Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
    	attributes.put(Keys.EMAIL_OTP, otp);
    	attributes.put(Keys.MOBILE_OTP, otp);
    	requestDTO.setData(new RequestData<>("", "", attributes));
    	
	    // Create user first
	    userService.createUser(requestDTO);
	    
	    @SuppressWarnings("unchecked")
		Map<String, String> requestAttributes = (Map<String, String>) requestDTO.getData().getAttributes();
	    
	    attributes.put(Keys.USER_MOBILE, requestAttributes.get(Keys.USER_MOBILE));
	    attributes.put(Keys.USER_EMAIL, "vishwasransingh@gmail.com");
	    // wrong password
	    attributes.put(Keys.USER_PASSWORD, "password1");
	    requestDTO.setData(new RequestData<>(null, null, attributes));
	    
	    assertThrows(BadRequestException.class, () -> userService.loginUser(requestDTO));
	}
	
	@Test
	void generateOtpTest() {
    	Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);
		
	    String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);
	    assertNotNull(otp);
	    
	    Optional<OtpDetail> otpDetail = otpDetailRepository.findByEmail(attributes.get(Keys.USER_EMAIL));
	    assertTrue(otpDetail.isPresent());
	    assertEquals(otp, otpDetail.get().getMobileOtp());
	}

	@Test
	void verifyOtpTest() {
		//TODO: Complete this after adding verification code
	}
	
	// TODO: add updateUserTest()
	// TODO: add deleteusertest()
	// TODO: add ofrgotPasswordTest()
	
}
