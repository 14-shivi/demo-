package com.dppl.mycards.card.controller.real_test;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRandomEmail;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserRequestAttributes;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.dppl.mycards.card.controller.UserController;
import com.dppl.mycards.card.repository.UserDetailsRepository;
import com.dppl.mycards.card.repository.UserRepository;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.repository.model.UserDetails;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.PasswordUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class UserControllerRealTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @MockBean
    private JwtValidator jwtService;

    @InjectMocks
    private UserController userController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(jwtService.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    void signUpTest() throws Exception {
    	Map<String, String> attributes = generateUserRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);
        String requestJson = objectMapper.writeValueAsString(requestDTO);
        
        // Generating otp first
        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/otp/generate-register")
                .header(Keys.REQUEST_ID, "101")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        String jsonResponse = result1.getResponse().getContentAsString();
        ResponseDTO response = objectMapper.readValue(jsonResponse, ResponseDTO.class);
        Map<String, String> result1Attributes = (Map<String, String>) response.getData().getAttributes();
        String otp = result1Attributes.get("OTP");
        attributes.put(Keys.EMAIL_OTP, otp);
        attributes.put(Keys.MOBILE_OTP, otp);
        
        // Signing up
        requestDTO = generateRequestDTO(attributes);
        requestJson = objectMapper.writeValueAsString(requestDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                .header(Keys.REQUEST_ID, "101")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseJson);
        
        JsonNode dataNode = rootNode.path("data").path("attributes");
        String userId = dataNode.path("id").asText();
        String mobile = dataNode.path("mobile").asText();
        String email = dataNode.path("email").asText();
        
        assertNotNull(userId, "User ID should not be null");
        assertEquals(attributes.get(Keys.USER_MOBILE), mobile);
        assertEquals(attributes.get(Keys.USER_EMAIL), email);
        
    }

    @Test
    void retrieveUserTest() throws Exception {    	
    	Map<String, String> attributes = generateUserRequestAttributes();
    	
    	// create first
		User user = userRepository.save(
				User.builder()
				.mobile(attributes.get(Keys.USER_MOBILE))
				.email(attributes.get(Keys.USER_EMAIL))
				.password(attributes.get(Keys.USER_PASSWORD))
				.createdTimestamp(LocalDateTime.now())
				.createdBy("vishwas-test")
				.build()
				);
		userDetailsRepository.save(
				UserDetails.builder()
				.userId(user.getUserId())
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.build()
				);
		
		// retrieve
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + user.getUserId())
		        .header(Keys.REQUEST_ID, "102")
		        .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(jsonPath("$.data.attributes.user.email").isNotEmpty())
		        .andExpect(jsonPath("$.data.attributes.user.email", is(user.getEmail())))
		        .andExpect(jsonPath("$.data.attributes.user.mobile").isNotEmpty())
		        .andExpect(jsonPath("$.data.attributes.user.mobile", is(user.getMobile())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    void updateUserTest() throws Exception {
    	// Create
    	Map<String, String> attributes = generateUserRequestAttributes();
        
        // create
		User user = userRepository.save(
				User.builder()
				.mobile(attributes.get(Keys.USER_MOBILE))
				.email(attributes.get(Keys.USER_EMAIL))
				.password(attributes.get(Keys.USER_PASSWORD))
				.createdTimestamp(LocalDateTime.now())
				.createdBy("vishwas-test")
				.build()
				);
	    userDetailsRepository.save(
				UserDetails.builder()
				.userId(user.getUserId())
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.build()
				);
			  
		  // Update
		String newEmail =  generateRandomEmail();
		attributes.put(Keys.USER_EMAIL, newEmail);
		RequestDTO requestDTO = generateRequestDTO(attributes);
	    String requestJson = objectMapper.writeValueAsString(requestDTO);
		MvcResult result = mockMvc.perform(put("/api/users/profile")
		        .header(Keys.REQUEST_ID, "103")
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestJson))
		        .andExpect(status().isOk())
		        .andReturn();		
		  
		String responseJson = result.getResponse().getContentAsString();
        ResponseDTO response = objectMapper.readValue(responseJson, ResponseDTO.class);
        
		assertEquals(newEmail, ((Map<String, String>)response.getData().getAttributes()).get(Keys.USER_EMAIL));
    }

    @Test
    void deleteUserTest() throws Exception {
    	Map<String, String> attributes = generateUserRequestAttributes();
    	
		// create
    	User user = userRepository.save(
    			User.builder()
				.mobile(attributes.get(Keys.USER_MOBILE))
				.email(attributes.get(Keys.USER_EMAIL))
				.password(attributes.get(Keys.USER_PASSWORD))
				.createdTimestamp(LocalDateTime.now())
				.createdBy("vishwas-test")
				.build()
    			);
	    userDetailsRepository.save(
				UserDetails.builder()
				.userId(user.getUserId())
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.build()
				);
    	
    	// delete
        mockMvc.perform(delete("/api/users/" + user.getUserId())
                .header(Keys.REQUEST_ID, "104")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.Status", is("Success")));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
    void loginTest() throws Exception {

        Map<String, String> attributes = generateUserRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);
    	
		// create
    	User user = userRepository.save(
    			User.builder()
				.mobile(attributes.get(Keys.USER_MOBILE))
				.email(attributes.get(Keys.USER_EMAIL))
				.password(PasswordUtil.encodePassword(attributes.get(Keys.USER_PASSWORD)))
				.createdTimestamp(LocalDateTime.now())
				.createdBy("vishwas-test")
				.build()
    			);
	    userDetailsRepository.save(
				UserDetails.builder()
				.userId(user.getUserId())
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.build()
				);
		
    	String requestJson = objectMapper.writeValueAsString(requestDTO);
		// generateOtpForLogin
	       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/otp/generate-login")
	               .header(Keys.REQUEST_ID, "101")
	               .contentType(MediaType.APPLICATION_JSON)
	               .content(requestJson))
	               .andExpect(MockMvcResultMatchers.status().isOk())
	               .andReturn();
	       String responseJson = result.getResponse().getContentAsString();
	       ResponseDTO response = objectMapper.readValue(responseJson, ResponseDTO.class);
	       String otp = ((Map<String, String>)response.getData().getAttributes()).get("OTP");
        
	       attributes.put(Keys.EMAIL_OTP, otp);
	       attributes.put(Keys.MOBILE_OTP, otp);
	       requestDTO = generateRequestDTO(attributes);
	       requestJson = objectMapper.writeValueAsString(requestDTO);
	       
	    // login
        mockMvc.perform(post("/api/users/signIn")
                .header(Keys.REQUEST_ID, "105")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.token").isNotEmpty())
                .andReturn(); 
    }    

    // These methods are being tested in loginTest(), forgotpasswordTest(), signupTest etc.
//    @Test
//    void generateOtpForNewRegistrationTest() throws Exception {
//    	/*
//    	 * {
//			  "data": {
//			    "id": "13453",
//			    "type": "user",
//			    "attributes": {
//			      "mobile": "8805265888",
//			      "email": "vishwasransing@gmail.com"
//			    }
//			  }
//			}
//    	 */
//    	Map<String, String> attributes = buildRequestAttributesHelperMethod();
//		RequestDTO requestDTO = buildRequestDtoHelperMethod(attributes);
//        String requestJson = objectMapper.writeValueAsString(requestDTO);
//        
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/otp/generate-register")
//                .header(Keys.REQUEST_ID, "101")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//    
//    @Test
//    void generateOtpForLoginTest() {
//    	/*
//    	 * {
//			  "data": {
//			    "id": "13453",
//			    "type": "user",
//			    "attributes": {
//			      "mobile": "8805265888",
//			      "email": "vishwasransing@gmail.com"
//			    }
//			  }
//			}
//    	 */
//    }
//    
//   @Test
//   void generateOtpForPasswordResetTest() {
//	   /*
//	    * {
//			  "data": {
//			    "id": "13453",
//			    "type": "user",
//			    "attributes": {
//			      "mobile": "8805265888",
//			      "email": "vishwasransing@gmail.com"
//			    }
//			  }
//		  }
//	    */
//   }
   
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Test
   void forgotPasswordtest() throws Exception {
	   
	   Map<String, String> attributes = generateUserRequestAttributes();
	   RequestDTO requestDTO = generateRequestDTO(attributes);
       String requestJson = objectMapper.writeValueAsString(requestDTO);
       
        // create
		User user = userRepository.save(
				User.builder()
					.mobile(attributes.get(Keys.USER_MOBILE))
					.email(attributes.get(Keys.USER_EMAIL))
					.password(PasswordUtil.encodePassword(attributes.get(Keys.USER_PASSWORD)))
					.createdTimestamp(LocalDateTime.now())
					.createdBy("vishwas-test")
			.build()
		);
	    userDetailsRepository.save(
				UserDetails.builder()
				.userId(user.getUserId())
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.build()
				);
       
       // Generating otp for reset password operation
       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/otp/generate-reset")
               .header(Keys.REQUEST_ID, "101")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestJson))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andReturn();
       
       String responseJson = result.getResponse().getContentAsString();
       ResponseDTO response = objectMapper.readValue(responseJson, ResponseDTO.class);
       String otp = ((Map<String, String>)response.getData().getAttributes()).get("OTP");
       
       attributes.put(Keys.EMAIL_OTP, otp);
       attributes.put(Keys.MOBILE_OTP, otp);
       requestDTO = generateRequestDTO(attributes);
       requestJson = objectMapper.writeValueAsString(requestDTO);
       
       // Forgot password request
       mockMvc.perform(MockMvcRequestBuilders.post("/api/users/password/reset")
               .header(Keys.REQUEST_ID, "101")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestJson))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andReturn();

   }

}
