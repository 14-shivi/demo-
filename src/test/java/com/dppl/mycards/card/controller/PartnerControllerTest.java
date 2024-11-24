package com.dppl.mycards.card.controller;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.service.PartnerService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerService partnerService;
    
    @MockBean
    private JwtValidator jwtValidator;
    
    @InjectMocks
    private PartnerController partnerController;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    	@BeforeEach
    	void setup() {
            Mockito.when(jwtValidator.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    	}

    @Test
    void signInTest() throws Exception {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        Map<String, Object> responseAttributes = generatePartnerResponseAttributes(
        		Partner.builder().id(1L).build());
        responseAttributes.put("token", "tokenxyz");
        when(partnerService.loginPartner(any())).thenReturn(responseAttributes);

        mockMvc.perform(post("/api/partners/banks/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.data.attributes.id").exists())
		        .andExpect(jsonPath("$.data.attributes.token").exists());
    }
    
    @Test
    void resetPasswordTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	String token = "tokenxyz";
    	requestAttributes.put("token", token);
    	
    	RequestDTO requestDTO = generateRequestDTO(requestAttributes);
    	when(partnerService.resetPassword(any())).thenReturn(token);
    	
    	mockMvc.perform(post("/api/partners/banks/password/reset")
	    			.contentType(MediaType.APPLICATION_JSON)
	    			.content(objectMapper.writeValueAsString(requestDTO)))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.data.attributes.token").exists())
    		.andExpect(jsonPath("$.data.attributes.token").value(token));
    }
    
    @Test
    void updateProfileTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(requestAttributes);
    	Partner partner = generatePartnerUsingAttributes(requestAttributes);
    	partner.setId(1L);
    	when(partnerService.updateProfile(any())).thenReturn(partner);
    	
    	mockMvc.perform(put("/api/partners/banks/profile")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(requestDTO))
	    			.header(Keys.REQUEST_ID, "501"))
    		.andExpect(jsonPath("$.data.attributes.id").exists());
    }
    
    @Test
    void signUpTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(requestAttributes);
    	Partner partner = generatePartnerUsingAttributes(requestAttributes);
    	partner.setId(1L);
    	when(partnerService.signUp(any())).thenReturn(partner);
    	
    	mockMvc.perform(post("/api/partners/banks/signUp")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(requestDTO))
	    			.header(Keys.REQUEST_ID, "502"))	
    		.andExpect(jsonPath("$.data.attributes.id").exists())
    		.andExpect(jsonPath("$.data.attributes.id").value("1"));
    }
    
    @Test
    void generateOtpTest() throws Exception{
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	requestAttributes.put(Keys.PARTNER_ID, "1");
    	RequestDTO requestDTO = generateRequestDTO(requestAttributes);
    	when(partnerService.generateOtp(any())).thenReturn(requestAttributes.get(Keys.EMAIL_OTP));
    	
    	mockMvc.perform(post("/api/partners/banks/otp/generate")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
			.andExpect(jsonPath("$.data.attributes").exists())
			.andExpect(jsonPath("$.data.attributes.otp").value(requestAttributes.get(Keys.EMAIL_OTP)));
    }
    	
    	@Test
    	void verifyOtpTest() throws Exception {
    	    Map<String, String> requestAttributes = Map.of(
    	        Keys.PARTNER_ID, "1",
    	        Keys.PARTNER_EMAIL_OTP, "123456",
    	        Keys.PARTNER_MOBILE_OTP, "654321"
    	    );
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    when(partnerService.verifyOtp(requestAttributes)).thenReturn(true);

    	    mockMvc.perform(post("/api/partners/banks/otp/verify")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO)))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes.isVerified").value("true"));
    	}

    	@Test
    	void createActivityTest() throws Exception {
    	    Map<String, String> requestAttributes = Map.of(
    	        Keys.PARTNER_ID, "1",
    	        Keys.PARTNER_ACTIVITY_EVENT_TYPE, "Sign-in",
    	        Keys.PARTNER_IP_ADDRESS, "192.168.1.1"
    	    );
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    PartnerActivity activity = new PartnerActivity(1L, 1L, "Sign-in", "192.168.1.1", LocalDateTime.now(), "");
    	    when(partnerService.saveActivity(requestAttributes)).thenReturn(activity);

    	    mockMvc.perform(post("/api/partners/banks/activity")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO))
    	            .header(Keys.REQUEST_ID, "503"))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.type").value(Keys.RESPONSE_TYPE_PARTNER_ACTIVITY))
    	        .andExpect(jsonPath("$.data.attributes.eventType").value("Sign-in"));
    	}

    	@Test
    	void getUserActivityForSpecificUserTest() throws Exception {
    	    String partnerId = "1";
    	    List<PartnerActivity> activities = List.of(
    	        new PartnerActivity(1L, 1L, "Sign-in", "192.168.1.1", LocalDateTime.now(), "")
    	    );

    	    when(partnerService.getActivityForSpecificPartner(eq(partnerId), any()))
    	        .thenReturn(activities);

    	    mockMvc.perform(post("/api/partners/banks/activity/" + partnerId)
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(generateRequestDTO(generatePartnerRequestAttributes())))
    	            .header(Keys.REQUEST_ID, "504"))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes[0].eventType").value("Sign-in"));
    	}

    	@Test
    	void getAllActivitiesBasedOnEventTypeTest() throws Exception {
    	    Map<String, String> requestAttributes = Map.of(Keys.PARTNER_ACTIVITY_EVENT_TYPE, "LOGIN");
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    List<PartnerActivity> activities = List.of(
    	        new PartnerActivity(1L, 1L, "Sign-in", "192.168.1.1", LocalDateTime.now(), ""),
    	        new PartnerActivity(2L, 2L, "Sign-in", "192.168.1.1", LocalDateTime.now(), "")
    	    );

    	    when(partnerService.getAllActivitiesBasedOnEventType(requestAttributes))
    	        .thenReturn(activities);

    	    mockMvc.perform(post("/api/partners/banks/activity/all")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO))
    	            .header(Keys.REQUEST_ID, "505"))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes[0].eventType").value("Sign-in"))
    	        .andExpect(jsonPath("$.data.attributes.length()").value(2));
    	}

    	@Test
    	void generateOtpForPasswordResetTest() throws Exception {
    	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    String otp = "123456";
    	    when(partnerService.generateOtp(requestAttributes, OtpContext.PASSWORD_RESET)).thenReturn(otp);

    	    mockMvc.perform(post("/api/partners/banks/otp/generate-reset")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO)))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
    	}

    	@Test
    	void generateOtpForNewRegistrationTest() throws Exception {
    	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    String otp = "654321";
    	    when(partnerService.generateOtp(requestAttributes, OtpContext.REGISTRATION)).thenReturn(otp);

    	    mockMvc.perform(post("/api/partners/banks/otp/generate-register")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO)))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
    	}

    	@Test
    	void generateOtpForLoginTest() throws Exception {
    	    Map<String, String> requestAttributes = generatePartnerRequestAttributes();
    	    RequestDTO requestDTO = generateRequestDTO(requestAttributes);

    	    String otp = "789012";
    	    when(partnerService.generateOtp(requestAttributes, OtpContext.LOGIN)).thenReturn(otp);

    	    mockMvc.perform(post("/api/partners/banks/otp/generate-login")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content(objectMapper.writeValueAsString(requestDTO)))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
    	}
    
}
