package com.dppl.mycards.card.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dppl.mycards.card.exception.BadRequestException;
import com.dppl.mycards.card.exception.ErrorResponse;
import com.dppl.mycards.card.exception.InternalServerErrorException;
import com.dppl.mycards.card.exception.NotFoundException;
import com.dppl.mycards.card.repository.CardRepository;
import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.CardService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.AESUtil;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.RequestParser;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import jakarta.transaction.Transactional;

@Service
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;
	private final AESUtil aesUtil;
	private static final Logger LOGGER = LogManager.getLogger(CardServiceImpl.class);

	public CardServiceImpl(CardRepository cardRepository, AESUtil aesUtil) {
		this.cardRepository = cardRepository;
		this.aesUtil = aesUtil;
	}

	@Override
	public Card addCard(RequestDTO requestDTO) {
		LOGGER.info("RequestId: {} :: Adding card", MDC.get(Keys.REQUEST_ID));
		Card card = new RequestParser().parseCard(requestDTO.getData().getAttributes());
		Card encryptedCard = getEncryptedCard(card);

		boolean cardExists = cardRepository.findByCardNumber(encryptedCard.getCardNumber()) != null;
		if (cardExists) {
			throw new BadRequestException(ErrorResponse.DUPLICATE_CARD);
		}
		if (isCardExpired(card.getExpiry())) {
			cardRepository.save(encryptedCard);
			throw new BadRequestException(ErrorResponse.CARD_EXPIRED);
		}

		return getDecryptedCard(cardRepository.save(encryptedCard));
	}
	
	public boolean isCardExpired(String expiryDate) {
	    String[] parts = expiryDate.split("/");

	    int expiryMonth = Integer.parseInt(parts[0]);
	    int expiryYear = Integer.parseInt(parts[1]) + 2000;
	    LocalDate expiryLocalDate = LocalDate.of(expiryYear, expiryMonth, 1)
	    		.plusMonths(1)
	    		.minusDays(1);

	    LocalDate currentDate = LocalDate.now();

	    return currentDate.isAfter(expiryLocalDate);
	}


	@Override
	public Card updateCard(RequestDTO requestDTO) {
		LOGGER.info("RequestId: {} :: Updating card", MDC.get(Keys.REQUEST_ID));
		Card updatedCard = new RequestParser().parseCard(requestDTO.getData().getAttributes());
		Card card;

		try {
			card = cardRepository.findByCardNumber(updatedCard.getCardNumber());
			if (card == null) {
				throw new NotFoundException(Keys.ENTITY_TYPE_CARD, "updateCard");
			}
		} catch (Exception e) {
			throw new InternalServerErrorException("database communication");
		}

		card.setBankName(aesUtil.encrypt(updatedCard.getBankName()));
		card.setCardName(aesUtil.encrypt(updatedCard.getCardName()));
		card.setCardType(aesUtil.encrypt(updatedCard.getCardType()));
		card.setExpiry(aesUtil.encrypt(updatedCard.getExpiry()));
		card.setIssueDate(aesUtil.encrypt(updatedCard.getIssueDate()));
		
		if (isCardExpired(updatedCard.getExpiry())) {
			cardRepository.save(card);
			throw new BadRequestException(ErrorResponse.CARD_EXPIRED);
		}

		return getDecryptedCard(cardRepository.save(card));
	}

	@Override
	@Transactional
	public void deleteCard(RequestDTO requestDTO) {
		LOGGER.info("RequestId: {} :: Deleting card", MDC.get(Keys.REQUEST_ID));
		Card card = new RequestParser().parseCard(requestDTO.getData().getAttributes());
		cardRepository.deleteByCardNumber(card.getCardNumber());
		LOGGER.info("RequestId: {} :: Deleted successfully", MDC.get(Keys.REQUEST_ID));
	}

	@Override
	public Card getCard(Long cardId) {
		LOGGER.info("RequestId: {} :: Retrieving card", MDC.get(Keys.REQUEST_ID));
		Optional<Card> encryptedCard = cardRepository.findById(cardId);

		if (encryptedCard.isPresent()) {
			return getDecryptedCard(encryptedCard.get());
		} else {
			throw new RuntimeException("Card not found");
		}
	}

	private Card getEncryptedCard(Card card) {
		LOGGER.info("RequestId: {} :: Encrypting card-info", MDC.get(Keys.REQUEST_ID));
		Card encryptedCard = new Card();
		encryptedCard.setBankName(aesUtil.encrypt(card.getBankName()));
		// Card number would be used for searching so it's not encrypted.
		encryptedCard.setCardNumber(card.getCardNumber());
		encryptedCard.setExpiry(aesUtil.encrypt(card.getExpiry()));
		encryptedCard.setIssueDate(aesUtil.encrypt(card.getIssueDate()));
		encryptedCard.setCardType(aesUtil.encrypt(card.getCardType()));
		encryptedCard.setCardName(aesUtil.encrypt(card.getCardName()));
		return encryptedCard;
	}

	private Card getDecryptedCard(Card card) {
		LOGGER.info("RequestId: {} :: Decrypting card-info", MDC.get(Keys.REQUEST_ID));
		card.setBankName(aesUtil.decrypt(card.getBankName()));
		// Card number would be used for searching so it's not encrypted.
//		card.setCardNumber(aesUtil.decrypt(card.getCardNumber()));
		card.setExpiry(aesUtil.decrypt(card.getExpiry()));
		card.setIssueDate(aesUtil.decrypt(card.getIssueDate()));
		card.setCardType(aesUtil.decrypt(card.getCardType()));
		card.setCardName(aesUtil.decrypt(card.getCardName()));
		return card;
	}

	@Override
	public List<Card> getCardsForUser(long userId) {
		LOGGER.info("RequestId: {} :: Fetching cards for userId: {}", MDC.get(Keys.REQUEST_ID), userId);
//		List<Card> cardsList = cardRepository.findAllByUserId();
		//TODO: Decrypt cardInfo and then return decrypted list
		
		return null;
	}

	@Override
	public List<Card> getAllCards() {
		LOGGER.info("RequestId: {} :: Fetching all cards ", MDC.get(Keys.REQUEST_ID));
		List<Card> cardsList = cardRepository.findAll();
		
		if (cardsList.isEmpty())
			return List.of();
		
		return cardsList.parallelStream().map(this::getDecryptedCard).toList();

	}
}
