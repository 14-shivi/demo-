package com.dppl.mycards.card.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.service.PartnerService;
import com.dppl.mycards.card.service.dto.DataDTO;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.RequestParser;
import com.dppl.mycards.card.utility.RequestValidator;

@RestController
@RequestMapping("/api/partners/banks")
@CrossOrigin
public class PartnerController {

    private static final Logger LOGGER = LogManager.getLogger();
    @Autowired
    private PartnerService partnerService;

    @PostMapping("/signIn")
    public ResponseEntity<ResponseDTO<Object>> signIn(
            @RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Partner sign-in request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
        new RequestValidator(requestAttributes)
                .hasEmail(Keys.PARTNER_EMAIL)
                .hasMobile(Keys.PARTNER_MOBILE)
                .hasPassword(Keys.PARTNER_PASSWORD)
                .hasOtp(Keys.PARTNER_EMAIL_OTP)
                .hasOtp(Keys.PARTNER_MOBILE_OTP)
                .hasString(Keys.PARTNER_IP_ADDRESS);

        Map<String, Object> responseAttributes = partnerService.loginPartner(requestAttributes);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, responseAttributes))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO<Object>> resetPassword(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Password reset request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
        new RequestValidator(requestAttributes)
                .hasEmail(Keys.PARTNER_EMAIL)
                .hasMobile(Keys.PARTNER_MOBILE)
                .hasOtp(Keys.PARTNER_EMAIL_OTP)
                .hasOtp(Keys.PARTNER_MOBILE_OTP)
                .hasString(Keys.PARTNER_IP_ADDRESS);

        String tempToken = partnerService.resetPassword(requestAttributes);

        Map<String, String> responseAttributes = Map.of("token", tempToken);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, responseAttributes))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO<Object>> updateProfile(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Update profile request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
		new RequestValidator(requestAttributes)
                .hasEmail(Keys.PARTNER_EMAIL)
                .hasMobile(Keys.PARTNER_MOBILE)
                .hasPassword(Keys.PARTNER_PASSWORD)
                .hasOtp(Keys.PARTNER_EMAIL_OTP)
                .hasOtp(Keys.PARTNER_MOBILE_OTP)
                .hasString(Keys.PARTNER_IP_ADDRESS)
                .hasString(Keys.PARTNER_COMPANY_NAME)
                .hasString(Keys.PARTNER_COMPANY_ADDRESS)
                .hasString(Keys.PARTNER_DATE_OF_INCORPORATION)
                .hasString(Keys.PARTNER_LICENSE)
                .hasString(Keys.PARTNER_CIN)
                .hasString(Keys.PARTNER_GST_NUMBER);

        Partner partner = partnerService.updateProfile(requestAttributes);

        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, partner))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signUp")
    public ResponseEntity<ResponseDTO<Object>> signUp(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: Signup request recieved.", MDC.get(Keys.REQUEST_ID));
        // TODO: add separate validator method for -> hasEitherEmailOrMobile() and hasEitherEmailOtpOrMobileOtp
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
        new RequestValidator(requestAttributes)
			.hasEmail(Keys.PARTNER_EMAIL)
			.hasMobile(Keys.PARTNER_MOBILE)
			.hasPassword(Keys.PARTNER_PASSWORD)
			.hasOtp(Keys.PARTNER_EMAIL_OTP)
			.hasOtp(Keys.PARTNER_MOBILE_OTP)
			.hasString(Keys.PARTNER_IP_ADDRESS)
			.hasString(Keys.PARTNER_COMPANY_NAME)
			.hasString(Keys.PARTNER_COMPANY_ADDRESS)
			.hasString(Keys.PARTNER_DATE_OF_INCORPORATION)
			.hasString(Keys.PARTNER_LICENSE)
			.hasString(Keys.PARTNER_CIN)
			.hasString(Keys.PARTNER_GST_NUMBER);

        Partner savedPartner = partnerService.signUp(requestAttributes);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, savedPartner))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/otp/generate")
    public ResponseEntity<ResponseDTO<Object>> generateOtp(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP generation request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
        new RequestValidator(requestAttributes)
                .hasLongId(Keys.PARTNER_ID);

        String otp = partnerService.generateOtp(requestAttributes);

        Map<String, String> attributes = Map.of("otp", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, attributes))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/otp/verify")
    public ResponseEntity<ResponseDTO<Object>> verifyOtp(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP verification request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
        new RequestValidator(requestAttributes)
                .hasLongId(Keys.PARTNER_ID)
                .hasOtp(Keys.PARTNER_EMAIL_OTP)
                .hasOtp(Keys.PARTNER_MOBILE_OTP);

        boolean isVerified = partnerService.verifyOtp(requestAttributes);

        Map<String, String> responseAttributes = Map.of("isVerified", Boolean.toString(isVerified));
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, responseAttributes))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/activity")
    public ResponseEntity<ResponseDTO<Object>> createActivity(@RequestBody RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: PartnerActivity creation request received", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
    	new RequestValidator(requestAttributes)
    	.hasLong(Keys.PARTNER_ID)
    	.hasString(Keys.PARTNER_ACTIVITY_EVENT_TYPE)
    	.hasString(Keys.PARTNER_IP_ADDRESS);
    	
        PartnerActivity partnerActivity = partnerService.saveActivity(requestAttributes);

        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER_ACTIVITY, partnerActivity))
                .build();

        return ResponseEntity.ok(response);
    }


	@PostMapping("/activity/{partnerId}")
    public ResponseEntity<?> getUserActivityForSpecificPartner( @PathVariable("partnerId") String partnerId,
            @RequestBody(required = false) RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: getActivityForSpecificPartner request received", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
    	// TODO: Add validation: hasUriParam() for uri parameters such as partnerId in this case.
//        new RequestValidator(requestAttributes)
//    	.hasString(Keys.PARTNER_ACTIVITY_EVENT_TYPE);
    	
    	List<PartnerActivity> partnerActivities = partnerService.getActivityForSpecificPartner(partnerId, requestAttributes);

        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER_ACTIVITY, partnerActivities))
                .build();

        return ResponseEntity.ok(response);
    }
    
	@PostMapping("/activity/all")
	public ResponseEntity<ResponseDTO<List<PartnerActivity>>> getAllActivitiesBasedOnEventType(
	        @RequestBody RequestDTO requestDTO) {
	    LOGGER.info("RequestId: {} :: getAllPartnerActivitiesBasedOnEventType request received", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO);
//	    new RequestValidator(requestAttributes)
//    	.hasString(Keys.PARTNER_ACTIVITY_EVENT_TYPE);
	    
	    List<PartnerActivity> partnerActivities = partnerService.getAllActivitiesBasedOnEventType(requestAttributes);

	    ResponseDTO<List<PartnerActivity>> response = ResponseDTO.<List<PartnerActivity>>builder()
	            .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER_ACTIVITY, partnerActivities))
	            .build();

	    return ResponseEntity.ok(response);
	}
	
    
    @PostMapping("/otp/generate-reset")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForPasswordReset(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP generation request for password reset recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO); 
        new RequestValidator(requestAttributes)
	        .hasEmail(Keys.PARTNER_EMAIL)
	        .hasMobile(Keys.PARTNER_MOBILE);

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.PASSWORD_RESET);

        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, attributes))
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/otp/generate-register")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForNewRegistration(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP generation request for new registration recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO); 
        new RequestValidator(requestAttributes)
	        .hasEmail(Keys.PARTNER_EMAIL)
	        .hasMobile(Keys.PARTNER_MOBILE);

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.REGISTRATION);

        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, attributes))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/generate-login")
    public ResponseEntity<ResponseDTO<Object>> generateOtpForLogin(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestID: {} :: OTP generation request for login recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = new RequestParser().parseAttributes(requestDTO); 
        new RequestValidator(requestAttributes)
	        .hasEmail(Keys.PARTNER_EMAIL)
	        .hasMobile(Keys.PARTNER_MOBILE);

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);

        Map<String, String> attributes = Map.of("OTP", otp);
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER, attributes))
                .build();

        return ResponseEntity.ok(response);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<ResponseDTO<Object>> retrieveUser(@PathVariable("id") Long id) {
//        LOGGER.info("RequestID: {} :: Retrieve user request recieved.", MDC.get(Keys.REQUEST_ID));
//
//        Map<String, Object> attributes = userService.getUserById(id);
//
//        ResponseDTO<Object> response = ResponseDTO.builder()
//                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER_ACTIVITY, Map.of(Keys.RESPONSE_TYPE_USER, attributes)))
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
    
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResponseDTO<Object>> deleteUser(@PathVariable("id") Long id) {
//        LOGGER.info("RequestID: {} :: Delete User request recieved.", MDC.get(Keys.REQUEST_ID));
//
//        userService.deleteUser(id);
//
//        ResponseDTO<Object> response = ResponseDTO.builder()
//                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_PARTNER_ACTIVITY, Map.of("Status", "Success")))
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
    

}
