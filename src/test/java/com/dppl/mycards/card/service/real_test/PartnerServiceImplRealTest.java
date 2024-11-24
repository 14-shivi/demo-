package com.dppl.mycards.card.service.real_test;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generatePartnerRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.PartnerActivityRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.impl.PartnerServiceImpl;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;

@SpringBootTest
class PartnerServiceImplRealTest {

    @Autowired
    private OtpDetailRepository otpDetailRepository;

    @Autowired
    private PartnerServiceImpl partnerService;
    
    @Autowired
    private PartnerActivityRepository partnerActivityRepository;
 

    @Test
    void loginPartnerTest() {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();

    	// generate password
        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
    	
        // create partner
        partnerService.signUp(requestAttributes);

        Map<String, Object> result = partnerService.loginPartner(requestAttributes);
        assertNotNull(result);
        assertNotNull(result.get("token"));
    }
    
    @Test
    void resetPasswordTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        
        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        
        partnerService.signUp(requestAttributes);

        String token = partnerService.resetPassword(requestAttributes);

        assertNotNull(token);

        List<PartnerActivity> activityList = partnerActivityRepository.findAll();
        boolean anyMatch = activityList.stream().anyMatch(activity -> activity.getEventType().equals(Keys.PASSWORD_RESET_ACTIVITY));
        assertTrue(anyMatch);
    }
    
    @Test
    void updateProfileTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);

        partnerService.signUp(requestAttributes);

        requestAttributes.put(Keys.PARTNER_COMPANY_NAME, "Updated Company");

        Partner updatedPartner = partnerService.updateProfile(requestAttributes);

        assertNotNull(updatedPartner);
        assertEquals("Updated Company", updatedPartner.getCompanyName());
    }
    
    @Test
    void generateOtpTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();

        String otp = partnerService.generateOtp(requestAttributes);

        assertNotNull(otp);

        List<OtpDetail> otpDetails = otpDetailRepository.findAll();
        boolean otpExists = otpDetails.stream().anyMatch(detail -> otp.equals(detail.getEmailOtp()));
        assertTrue(otpExists);
    }

    /*@Test
    void verifyOtpTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        Partner partner = partnerService.signUp(requestAttributes);
        
        requestAttributes.put(Keys.PARTNER_ID, Long.toString(partner.getId()));

        otp = partnerService.generateOtp(requestAttributes);

        requestAttributes.put(Keys.EMAIL_OTP, otp);

        boolean isVerified = partnerService.verifyOtp(requestAttributes);

        assertTrue(isVerified);
    }*/

    @Test
    void saveActivityTest() {
        Map<String, String> requestAttributes = Map.of(
            "eventType", "TEST_ACTIVITY",
            "eventDetails", "Test activity details",
            Keys.PARTNER_IP_ADDRESS, "127.0.0.1"
        );

        PartnerActivity activity = partnerService.saveActivity(requestAttributes);

        assertNotNull(activity);
        assertEquals("TEST_ACTIVITY", activity.getEventType());
    }

    @Test
    void getActivityForSpecificPartnerTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        
        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        Partner partner = partnerService.signUp(requestAttributes);

        requestAttributes.put("startDate", "2024-01-01");
        requestAttributes.put("endDate", "2024-12-31");

        List<PartnerActivity> activities = partnerService.getActivityForSpecificPartner(partner.getId().toString(), requestAttributes);

        assertNotNull(activities);
    }

    @Test
    void getAllActivitiesBasedOnEventTypeTest() {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();

        String otp = partnerService.generateOtp(requestAttributes, OtpContext.LOGIN);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        Partner partner = partnerService.signUp(requestAttributes);

        partnerService.saveActivity(Map.of(
            "eventType", Keys.PASSWORD_RESET_ACTIVITY,
            "eventDetails", "Password reset activity",
            Keys.PARTNER_IP_ADDRESS, "127.0.0.1"
        ));

        Map<String, String> eventTypeRequest = Map.of(Keys.PARTNER_ACTIVITY_EVENT_TYPE, Keys.PASSWORD_RESET_ACTIVITY);

        List<PartnerActivity> activities = partnerService.getAllActivitiesBasedOnEventType(eventTypeRequest);

        assertNotNull(activities);
        assertTrue(activities.stream().anyMatch(activity -> Keys.PASSWORD_RESET_ACTIVITY.equals(activity.getEventType())));
    }

}
