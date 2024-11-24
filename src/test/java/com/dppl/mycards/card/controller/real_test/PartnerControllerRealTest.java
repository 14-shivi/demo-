package com.dppl.mycards.card.controller.real_test;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generatePartnerActivityRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generatePartnerRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.UrlPaths;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
class PartnerControllerRealTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtValidator jwtValidator;

    @BeforeEach
    public void setup() {
    	MockitoAnnotations.openMocks(this);
        Mockito.when(jwtValidator.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    }

	@Test
    void signInTest() throws Exception {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        // signup otp request
        String otp = generateOtpHelperMethod(requestDTO, OtpContext.LOGIN);
        
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        requestDTO = generateRequestDTO(requestAttributes);

        
        // signup request
        mockMvc.perform(post("/api/partners/banks/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk()) 
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists()) 
        .andExpect(jsonPath("$.data.attributes").isNotEmpty())
        .andExpect(jsonPath("$.data.attributes.id").isNotEmpty());

        // signin request
        mockMvc.perform(post("/api/partners/banks/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) 
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists()) 
                .andExpect(jsonPath("$.data.attributes").isNotEmpty())
        		.andExpect(jsonPath("$.data.attributes.token").isNotEmpty());

    }

    @Test
    void passwordResetTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        // signup otp request
        String otp = generateOtpHelperMethod(requestDTO, OtpContext.LOGIN);
        
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        requestDTO = generateRequestDTO(requestAttributes);        
        // signup request
        mockMvc.perform(post("/api/partners/banks/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk()) 
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists()) 
        .andExpect(jsonPath("$.data.attributes").isNotEmpty())
        .andExpect(jsonPath("$.data.attributes.id").isNotEmpty());
        
        mockMvc.perform(post("/api/partners/banks/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attributes.token").isNotEmpty());
    }
    
    @Test
    void updateProfileTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        // signup otp request
        String otp = generateOtpHelperMethod(requestDTO, OtpContext.LOGIN);
        
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        requestDTO = generateRequestDTO(requestAttributes);
        
        // signup request
        mockMvc.perform(post("/api/partners/banks/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk()) 
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists()) 
        .andExpect(jsonPath("$.data.attributes").isNotEmpty())
        .andExpect(jsonPath("$.data.attributes.id").isNotEmpty());

        // profile update
        requestAttributes.put(Keys.PARTNER_COMPANY_NAME, "ABC Company");
        requestDTO = generateRequestDTO(requestAttributes);
        mockMvc.perform(put("/api/partners/banks/profile")
        		.header(Keys.REQUEST_ID, "501")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attributes").isNotEmpty())
            .andExpect(jsonPath("$.data.attributes.companyName").value("ABC Company"));
    }

    @Test
    void generateOtpTest() throws Exception {
        Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        requestAttributes.put(Keys.PARTNER_ID, "1");

		RequestDTO requestDTO = generateRequestDTO(requestAttributes);
        
        // OTP generate endpoint
        mockMvc.perform(post("/api/partners/banks/otp/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists()) 
            .andExpect(jsonPath("$.data.attributes.otp").isNotEmpty());
    }

    /*@SuppressWarnings("unchecked")
	@Test
    void verifyOtpTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        // signup otp request
        String otp = generateOtpHelperMethod(requestDTO, OtpContext.LOGIN);
        
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        requestDTO = generateRequestDTO(requestAttributes);
    	// First, create record
    	MvcResult result = mockMvc.perform(
    	        post("/api/partners/banks/signUp")
    	                .contentType(MediaType.APPLICATION_JSON)
    	                .content(objectMapper.writeValueAsString(requestDTO))
    	        )
    	        .andExpect(status().isOk())
    	        .andReturn();
    	String resultJson = result.getResponse().getContentAsString();
    	ResponseDTO<?> responseDTO = objectMapper.readValue(resultJson, ResponseDTO.class);
    	Map<String, Object> responseAttributes = (Map<String, Object>) responseDTO.getData().getAttributes();
    	String partnerId = responseAttributes.get(Keys.PARTNER_ID).toString();

    	otp = generateOtpHelperMethod(requestDTO, null);
    	
    	requestAttributes = generatePartnerRequestAttributes();
        requestAttributes.put(Keys.PARTNER_ID, partnerId);
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);

		requestDTO = generateRequestDTO(requestAttributes);
        // OTP verification endpoint
        mockMvc.perform(post("/api/partners/banks/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attributes.isVerified").value("true"));
    }*/

    @Test
    void createActivityTest() throws Exception {
        Map<String, String> requestAttributes = generatePartnerActivityRequestAttributes();
        requestAttributes.put(Keys.PARTNER_ID, "1");
		RequestDTO requestDTO = generateRequestDTO(requestAttributes);
        
        // create activity endpoint
        mockMvc.perform(post("/api/partners/banks/activity")
        		.header(Keys.REQUEST_ID, "504")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk()) 
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attributes.eventType")
            		.value(requestAttributes.get(Keys.PARTNER_ACTIVITY_EVENT_TYPE)));
    }

    @SuppressWarnings("unchecked")
	@Test
    void getActivityForSpecificPartnerTest() throws Exception {
    	Map<String, String> requestAttributes = generatePartnerRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(requestAttributes);

        // signup otp request
        String otp = generateOtpHelperMethod(requestDTO, OtpContext.LOGIN);
      
        requestAttributes.put(Keys.EMAIL_OTP, otp);
        requestAttributes.put(Keys.MOBILE_OTP, otp);
        requestDTO = generateRequestDTO(requestAttributes);
  	    // First, create record
  	    MvcResult result = mockMvc.perform(
  	        post("/api/partners/banks/signUp")
  	                .contentType(MediaType.APPLICATION_JSON)
  	                .content(objectMapper.writeValueAsString(requestDTO))
  	        )
  	        .andExpect(status().isOk())
  	        .andReturn();
	  	String resultJson = result.getResponse().getContentAsString();
	  	ResponseDTO<?> responseDTO = objectMapper.readValue(resultJson, ResponseDTO.class);
	  	Map<String, Object> responseAttributes = (Map<String, Object>) responseDTO.getData().getAttributes();
	  	String partnerId = responseAttributes.get(Keys.PARTNER_ID).toString();
        
		// create activity endpoint
		requestAttributes = generatePartnerActivityRequestAttributes();
		requestAttributes.put(Keys.PARTNER_ID, partnerId);
		requestDTO = generateRequestDTO(requestAttributes);
		mockMvc.perform(post("/api/partners/banks/activity")
		.header(Keys.REQUEST_ID, "504")
		.contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(requestDTO)))
		.andExpect(status().isOk()); 
        
		// retrieve
        mockMvc.perform(post("/api/partners/banks/activity/{partnerId}", partnerId)
        		.header(Keys.REQUEST_ID, "505")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists()) 
            .andExpect(jsonPath("$.data.attributes").isArray());
    }

    @Test
    void getAllActivitiesBasedOnEventTypeTest() throws Exception {
        Map<String, String> requestAttributes = generatePartnerActivityRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(requestAttributes);
        
        mockMvc.perform(post("/api/partners/banks/activity/all")
        		.header(Keys.REQUEST_ID, "506")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attributes").isArray());
    }


	@SuppressWarnings("unchecked")
	private String generateOtpHelperMethod(RequestDTO requestDTO, OtpContext otpContext)
			throws Exception {
		
		String url = switch (otpContext) {
		case REGISTRATION -> UrlPaths.GENERATE_OTP_REGISTRATION;
		case LOGIN -> UrlPaths.GENERATE_OTP_LOGIN;
		case PASSWORD_RESET -> UrlPaths.GENERATE_OTP_PASSWORD_RESET;
		default -> UrlPaths.GENERATE_OTP_NO_CONTEXT;
		};
		
		MvcResult mvcResult = mockMvc.perform(post(url)
        		.header(Keys.REQUEST_ID, "101")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        	.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ResponseDTO<?> response = objectMapper.readValue(jsonResponse, ResponseDTO.class);
        Map<String, ?> attributes = (Map<String, ?>) response.getData().getAttributes();
        return (String) attributes.get("OTP");
	}
    

}