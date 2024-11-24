package com.dppl.mycards.card.controller.real_test;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.dppl.mycards.card.controller.UserActivityController;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.Keys;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
class UserActivityControllerRealTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtValidator jwtService;

    @InjectMocks
    private UserActivityController userActivityController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(jwtService.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    }

    @Test
    void createUserActivityTest() throws Exception {
       
    	RequestDTO requestDTO = requestBuilderHelperMethod();
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        @SuppressWarnings("unchecked")
		Map<String, String> attributes = (Map<String, String>) requestDTO.getData().getAttributes();
        String id = attributes.get(Keys.USER_ID);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/activity")
                .header(Keys.REQUEST_ID, "200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.eventType", Matchers.is("sign-in")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.eventDetails", Matchers.is("Signin activity details")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.userId").value(Long.parseLong(id)))
                .andReturn();
        
        System.out.println("\n\n" + result.getResponse().getContentAsString() + "\n\n");
    }
    
    private RequestDTO requestBuilderHelperMethod() {
        	
    	Map<String, String> attributes = new HashMap<>();
    	attributes.put(Keys.USER_ID, "1");
    	attributes.put(Keys.USER_ACTIVITY_EVENT_TYPE, "sign-in");
    	attributes.put(Keys.USER_ACTIVITY_EVENT_DETAILS, "Signin activity details");
    	attributes.put(Keys.USER_ACTIVITY_IP_ADDRESS, "192.168.9.5");
    	attributes.put(Keys.USER_EMAIL, "user12@gmail.com");
    	attributes.put(Keys.USER_MOBILE, "1234567890");

        RequestData<Object> requestData = new RequestData<>();
        requestData.setAttributes(attributes);
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setData(requestData);
        
        return requestDTO;
    }

    @SuppressWarnings("unchecked")
	@Test
    void getUserActivityForSpecificUserTest() throws Exception {
        RequestDTO requestDTO = requestBuilderHelperMethod();
        String requestJson = objectMapper.writeValueAsString(requestDTO);
//        Map<String, String> attributes = (Map<String, String>) requestDTO.getData().getAttributes();
//        String userId = attributes.get(Keys.USER_ID);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity")
                .header(Keys.REQUEST_ID, "200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity/{param}", Keys.USER_MOBILE)
                .header(Keys.REQUEST_ID, "201")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.attributes").isNotEmpty())
                .andExpect(jsonPath("$.data.attributes[0].activityId").isNotEmpty())
                .andExpect(jsonPath("$.data.attributes[0].eventType").isNotEmpty());
    }

    @Test
    void getAllUserActivitiesBasedOnEventTypeTest() throws Exception {
    	RequestDTO requestDTO = requestBuilderHelperMethod();
        String requestJson = objectMapper.writeValueAsString(requestDTO);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity")
                .header(Keys.REQUEST_ID, "200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(post("/api/activity")
                .header(Keys.REQUEST_ID, "202")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.attributes").isNotEmpty());
    }
}
