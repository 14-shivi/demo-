package com.dppl.mycards.card.service.real_test;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateCardRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dppl.mycards.card.repository.CardRepository;
import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.impl.CardServiceImpl;
import com.dppl.mycards.card.utility.AESUtil;
import com.dppl.mycards.card.utility.Keys;

@SpringBootTest
class CardServiceImplRealTest {
	
	@Autowired
	private CardServiceImpl cardService;
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private AESUtil aesUtil;
	
	@Test
	void addCardTest() {
		RequestDTO requestDTO = generateRequestDTO(generateCardRequestAttributes());
		Card card = cardService.addCard(requestDTO);
		
		Optional<Card> cardOptional = cardRepository.findById(card.getCardId());
		
		assert(!cardOptional.isEmpty());
		assertEquals(card.getCardNumber(), cardOptional.get().getCardNumber());
	}
	
	@Test
	void updateCardTest() {
		RequestDTO requestDTO = generateRequestDTO(generateCardRequestAttributes());
		
		Card createdCard = cardService.addCard(requestDTO);
		
		@SuppressWarnings("unchecked")
		Map<String, String> attributes = (Map<String, String>) requestDTO.getData().getAttributes();
		attributes.put(Keys.BANK_NAME, "UpdatedBankName");
		requestDTO.setData(new RequestData<>("", "", attributes));
		Card updatedCard = cardService.updateCard(requestDTO);
		
		assertNotEquals(createdCard, updatedCard);
		assertEquals("UpdatedBankName", updatedCard.getBankName());
	}
	
	@Test
	void deleteCardTest() {
		RequestDTO requestDTO = generateRequestDTO(generateCardRequestAttributes());
		
		Card card = cardService.addCard(requestDTO);
		
		cardService.deleteCard(requestDTO);
	
		assertNull(cardRepository.findByCardNumber(card.getCardNumber()));
	}
}
