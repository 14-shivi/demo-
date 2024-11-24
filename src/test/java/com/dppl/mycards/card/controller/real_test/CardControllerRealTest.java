package com.dppl.mycards.card.controller.real_test;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateCardRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.dppl.mycards.card.controller.CardController;
import com.dppl.mycards.card.service.CardService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.AESUtil;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.Keys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
class CardControllerRealTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CardService cardService;
    
    @MockBean
    private JwtValidator jwtService;

    @InjectMocks
    private CardController cardController;
    
    @Autowired
    private AESUtil aesUtil;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(jwtService.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    }

	@SuppressWarnings("unchecked")
	@Test
    void addCardTest() {        
		Map<String, String> attributes = generateCardRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	String cardNumber = attributes.get(Keys.CARD_NUMBER);
    	
        String requestJson = null;
		try {
			requestJson = objectMapper.writeValueAsString(requestDTO);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		
        try {
			MvcResult result = mockMvc.perform(post("/api/cards")
					.header(Keys.REQUEST_ID, "101")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(requestJson))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.cardNumber", is(cardNumber)))
			        .andReturn();
			
			String jsonResponse = result.getResponse().getContentAsString();
	        ResponseDTO<?> response = objectMapper.readValue(jsonResponse, ResponseDTO.class);
			
	        Map<String, String> resultAttributes = (Map<String, String>) response.getData().getAttributes();
	        
	        assertNotNull(resultAttributes);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @SuppressWarnings("unchecked")
	@Test
    void updateCardTest() {
		Map<String, String> attributes = generateCardRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);

        try {
			String requestJson = objectMapper.writeValueAsString(requestDTO);
			// Create 
			MvcResult result1 = mockMvc.perform(post("/api/cards")
					.header(Keys.REQUEST_ID, "101")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(requestJson))
			        .andExpect(status().isOk())
			        .andReturn();


			// Update 
			attributes.put( Keys.CARD_TYPE, "credit");
			requestDTO.setData(new RequestData<>("1", "cards", attributes));
			String updateRequestJson = objectMapper.writeValueAsString(requestDTO);
			MvcResult result2 = mockMvc.perform(put("/api/cards")
					.header(Keys.REQUEST_ID, "100")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(updateRequestJson))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.cardNumber", Matchers.is(attributes.get(Keys.CARD_NUMBER))))
			        .andReturn();

			String jsonResponse = result2.getResponse().getContentAsString();
	        ResponseDTO<?> response = objectMapper.readValue(jsonResponse, ResponseDTO.class);
			
	        Map<String, String> resultAttributes = (Map<String, String>) response.getData().getAttributes();
	        
			assertEquals("credit", aesUtil.decrypt(resultAttributes.get(Keys.CARD_TYPE)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Test
    void deleteCardTest() throws Exception {
    	// Create
		Map<String, String> attributes = generateCardRequestAttributes();
		RequestDTO requestDTO = generateRequestDTO(attributes);

        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/cards")
        		.header(Keys.REQUEST_ID, "102")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        // Delete
        mockMvc.perform(delete("/api/cards")
        		.header(Keys.REQUEST_ID, "102")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes", is("Card deleted successfully")));
    }
    
    // TODO: Add getcardsTest()
}
