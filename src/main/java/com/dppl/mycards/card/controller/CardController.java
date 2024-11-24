package com.dppl.mycards.card.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.CardService;
import com.dppl.mycards.card.service.dto.DataDTO;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.RequestParser;
import com.dppl.mycards.card.utility.RequestValidator;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("api/cards")
@Tag(name = "Card-Controller", description = "CRUD operations for cards")
public class CardController {

    private CardService cardService;
    	
	public CardController(CardService cardService) {
		super();
		this.cardService = cardService;
	}

	private static final Logger LOGGER = LogManager.getLogger();

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> addCard(@RequestBody RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: Add card request recieved.", MDC.get(Keys.REQUEST_ID));
    	new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasString(Keys.BANK_NAME)
    	.hasCardNumber(Keys.CARD_NUMBER)
    	.hasString(Keys.CARD_EXPIRY_DATE)
    	.hasString(Keys.CARD_ISSUE_DATE)
    	.hasString(Keys.CARD_TYPE)
    	.hasString(Keys.CARD_NAME);
    	
    	Card savedCard = cardService.addCard(requestDTO);
    	
    	ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, savedCard))
    			.build();

    	return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<Object>> updateCard(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestId: {} :: Update card request received.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasString(Keys.BANK_NAME)
    	.hasCardNumber(Keys.CARD_NUMBER)
    	.hasString(Keys.CARD_EXPIRY_DATE)
    	.hasString(Keys.CARD_ISSUE_DATE)
    	.hasString(Keys.CARD_TYPE)
    	.hasString(Keys.CARD_NAME);
        
        Card updatedCard = cardService.updateCard(requestDTO);
        
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, updatedCard))
                .build();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping
    public ResponseEntity<ResponseDTO<Object>> deleteCard(@RequestBody RequestDTO requestDTO) {
        LOGGER.info("RequestId: {} :: Delete card request received.", MDC.get(Keys.REQUEST_ID));
        new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasString(Keys.BANK_NAME)
    	.hasCardNumber(Keys.CARD_NUMBER)
    	.hasString(Keys.CARD_EXPIRY_DATE)
    	.hasString(Keys.CARD_ISSUE_DATE)
    	.hasString(Keys.CARD_TYPE)
    	.hasString(Keys.CARD_NAME);
        
        cardService.deleteCard(requestDTO);
        
        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, "Card deleted successfully"))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{cardId}")
    public ResponseEntity<ResponseDTO<Object>> getCard(@PathVariable("cardId") String cardId) {
    	LOGGER.info("RequestId: {} :: Get card request recieved.", MDC.get(Keys.REQUEST_ID));
    	Card card = cardService.getCard(Long.parseLong(cardId));
    	
    	ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, card))
    			.build();
    	
    	return ResponseEntity.ok(response);
    }
    
//    @GetMapping("/{userId}")
//    public ResponseEntity<ResponseDTO<Object>> getCardsForUser(@PathVariable("userId") String userId) {
//    	LOGGER.info("RequestId: {} :: Get card for a user request recieved.", MDC.get(Keys.REQUEST_ID));
//    	List<Card> cardsList = cardService.getCardsForUser(Long.parseLong(userId));
//    	
//    	ResponseDTO<Object> response = ResponseDTO.builder()
//    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, cardsList))
//    			.build();
//    	
//    	return ResponseEntity.ok(response);
//    }
    
    @GetMapping
    public ResponseEntity<ResponseDTO<Object>> getAllCards() {
    	LOGGER.info("RequestId: {} :: Get all cards request recieved.", MDC.get(Keys.REQUEST_ID));
    	List<Card> cardsList = cardService.getAllCards();
    	
    	ResponseDTO<Object> response = ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_CARD, Map.of("cards", cardsList)))
    			.build();
    	
    	return ResponseEntity.ok(response);
    }

}
