package com.dppl.mycards.card.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateCardRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.CardService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.JwtValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtValidator jwtService;

    @InjectMocks
    private CardController cardController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
        Mockito.when(jwtService.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
    }

    @Test
    void addCardTest() throws Exception {
    	Map<String, String> attributes = generateCardRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);
        Card card = new Card();

        when(cardService.addCard(requestDTO)).thenReturn(card);

        mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void updateCardTest() throws Exception {
    	Map<String, String> attributes = generateCardRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);
        Card card = new Card(); 

        when(cardService.updateCard(requestDTO)).thenReturn(card);

        mockMvc.perform(put("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void deleteCardTest() throws Exception {
    	Map<String, String> attributes = generateCardRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);

        mockMvc.perform(delete("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.attributes").value("Card deleted successfully"));
    }
}
