package com.dppl.mycards.card.controller;

import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.UserService;
import com.dppl.mycards.card.service.dto.DataDTO;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.RequestParser;
import com.dppl.mycards.card.utility.RequestValidator;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@Tag(name = "User-Controller", description = "CRUD operations for user + Login operation")
public class UserController {
	
	private static final Logger LOGGER = LogManager.getLogger();

    private UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<Object>> createUser(@RequestBody RequestDTO requestDTO) {
    	LOGGER.info("RequestID: {} :: Signup request recieved.", MDC.get(Keys.REQUEST_ID));
    	// TODO: add separate validator method for -> hasEitherEmailOrMobile() and hasEitherEmailOtpOrMobileOtp
    	new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE)
    	.hasPassword(Keys.USER_PASSWORD)
    	.hasOtp(Keys.EMAIL_OTP)
    	.hasOtp(Keys.MOBILE_OTP)
    	.hasString(Keys.USER_ACTIVITY_IP_ADDRESS)
    	.hasName(Keys.USER_DETAILS_FIRST_NAME)
    	.hasName(Keys.USER_DETAILS_LAST_NAME)
    	.hasBoolean(Keys.USER_DETAILS_TERMS_ACCEPTED)
    	.hasGender(Keys.USER_DETAILS_GENDER)
    	.hasDate(Keys.USER_DETAILS_DOB)
    	.hasSalutation(Keys.USER_DETAILS_SALUTATION);
    	
    	User savedUser = userService.createUser(requestDTO);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, savedUser))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Object>> retrieveUser(@PathVariable("id") Long id) {
        LOGGER.info("RequestID: {} :: Retrieve user request recieved.", MDC.get(Keys.REQUEST_ID));
        
        Map<String, Object> attributes = userService.getUserById(id);
        
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, Map.of(Keys.RESPONSE_TYPE_USER, attributes)))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO<Object>> updateUser(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Update profile request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE)
    	.hasPassword(Keys.USER_PASSWORD)
    	.hasOtp(Keys.EMAIL_OTP)
    	.hasOtp(Keys.MOBILE_OTP)
    	.hasString(Keys.USER_ACTIVITY_IP_ADDRESS)
    	.hasName(Keys.USER_DETAILS_FIRST_NAME)
    	.hasName(Keys.USER_DETAILS_LAST_NAME)
    	.hasBoolean(Keys.USER_DETAILS_TERMS_ACCEPTED)
    	.hasGender(Keys.USER_DETAILS_GENDER)
    	.hasDate(Keys.USER_DETAILS_DOB)
    	.hasSalutation(Keys.USER_DETAILS_SALUTATION);
        
        User updatedUser = userService.updateUser(requestDTO);
        updatedUser.setPassword(null);
        
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, updatedUser))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Object>> deleteUser(@PathVariable("id") Long id) {
        LOGGER.info("RequestID: {} :: Delete User request recieved.", MDC.get(Keys.REQUEST_ID));

        userService.deleteUser(id);
        
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, Map.of("Status", "Success")))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ResponseDTO<Object>> login(
        @RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Login request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
        .hasEmail(Keys.USER_EMAIL)
        .hasMobile(Keys.USER_MOBILE)
        .hasPassword(Keys.USER_PASSWORD)
        .hasOtp(Keys.EMAIL_OTP)
    	.hasOtp(Keys.MOBILE_OTP)
    	.hasString(Keys.USER_ACTIVITY_IP_ADDRESS);
        
        Map<String, Object> attributes = userService.loginUser(requestDTO);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/generate-register")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForNewRegistration(@RequestBody RequestDTO requestDTO) {   
        LOGGER.info("RequestID: {} :: OTP generation request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE);
        
        String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);  
        
        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/otp/generate-login")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForLogin(@RequestBody RequestDTO requestDTO) {  
        LOGGER.info("RequestID: {} :: OTP generation request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE);
        
        String otp = userService.generateOtp(requestDTO, OtpContext.LOGIN);  
        
        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/otp/generate-reset")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForPasswordReset(@RequestBody RequestDTO requestDTO) {   
        LOGGER.info("RequestID: {} :: OTP generation request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE);
        
        String otp = userService.generateOtp(requestDTO, OtpContext.PASSWORD_RESET);  
        
        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ResponseDTO<Object>> verifyOtp(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP verification request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE)
    	.hasOtp(Keys.EMAIL_OTP)
    	.hasOtp(Keys.MOBILE_OTP);
        
        boolean isVerified = userService.verifyOtp(requestDTO);
        
        Map<String, String> attributes = Map.of("isVerified", Boolean.toString(isVerified));
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);          
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO<Object>> forgotPassword(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Password reset request recieved.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
        .hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE)
    	.hasOtp(Keys.EMAIL_OTP)
    	.hasOtp(Keys.MOBILE_OTP)
    	.hasString(Keys.USER_ACTIVITY_IP_ADDRESS);
        
        String tempToken = userService.forgotPassword(requestDTO);
        
        Map<String, String> attributes = Map.of("token", tempToken);
        ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER, attributes))
    			.build();
    	
        return ResponseEntity.ok(response);
    }
    

 
}
