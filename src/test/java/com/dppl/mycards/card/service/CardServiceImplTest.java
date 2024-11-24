package com.dppl.mycards.card.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dppl.mycards.card.repository.CardRepository;
import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.impl.CardServiceImpl;
import com.dppl.mycards.card.utility.AESUtil;
import com.dppl.mycards.card.utility.Keys;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    
    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private CardServiceImpl cardService;

    private RequestDTO requestDTO;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        requestDTO = new RequestDTO();  
        
        card = new Card();
        card.setCardNumber("1212121212121212");
        card.setBankName("Bank ABC");
        card.setCardName("plat");
        card.setCardType("Credit");
        card.setExpiry("12/30");
        card.setIssueDate("12/20");
    }

    @Test
    void addCardTest_Success() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Keys.CARD_NUMBER, card.getCardNumber());
        attributes.put(Keys.BANK_NAME, card.getBankName());
        attributes.put(Keys.CARD_NAME, card.getCardName());
        attributes.put(Keys.CARD_TYPE, card.getCardType());
        attributes.put(Keys.CARD_EXPIRY_DATE, card.getExpiry());
        attributes.put(Keys.CARD_ISSUE_DATE, card.getIssueDate());

        requestDTO.setData(new RequestData<>("1", "card", attributes));

        when(cardRepository.findByCardNumber(card.getCardNumber())).thenReturn(null);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card createdCard = cardService.addCard(requestDTO);

        assertNotNull(createdCard);
        assertEquals(card, createdCard);
    }


    @Test
    void addCardTest_CardAlreadyExists() {
        when(cardRepository.findByCardNumber(card.getCardNumber())).thenReturn(card);

        assertThrows(RuntimeException.class, () -> cardService.addCard(requestDTO));
    }

    @Test
    void updateCardTest_Success() {
        Map<String, String> attributes = new HashMap<>();
        String updatedBankName = "ABCD Bank";
        attributes.put(Keys.CARD_NUMBER, card.getCardNumber());
        attributes.put(Keys.BANK_NAME, updatedBankName);
        attributes.put(Keys.CARD_EXPIRY_DATE, card.getExpiry());

        requestDTO.setData(new RequestData<>("1", "card", attributes));

        when(cardRepository.findByCardNumber(card.getCardNumber())).thenReturn(card);

        Card updatedCard = Card.builder()
                .bankName(updatedBankName)
                .cardNumber(card.getCardNumber())
                .expiry(card.getExpiry())
                .issueDate(card.getIssueDate())
                .cardType(card.getCardType())
                .cardName(card.getCardName())
                .build();

        when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);
        
        Card resultCard = cardService.updateCard(requestDTO);
System.err.println(resultCard);
        assertNotNull(resultCard);
    }


    @Test
    void updateCardTest_CardNotFound() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            cardService.updateCard(requestDTO);
        });
    }

    @Test
    void deleteCardTest_Success() {
    	
    	Map<String, String> attributes = new HashMap<>();
        attributes.put(Keys.CARD_NUMBER, card.getCardNumber());

        requestDTO.setData(new RequestData("1", "card", attributes));
    	
        when(cardRepository.findByCardNumber(card.getCardNumber())).thenReturn(card);

        assertDoesNotThrow(() -> {
            cardService.deleteCard(requestDTO);
        });
    }

//    TODO: Have to exception to be thrown from CardServiceImpl when a record is not found
//    @Test
//    void deleteCardTest_CardNotFound() {
//        when(cardRepository.findByCardNumber(anyString())).thenReturn(null);
//        assertThrows(RecordNotFoundException.class, () -> cardService.deleteCard(requestDTO));
//    }
    
}
