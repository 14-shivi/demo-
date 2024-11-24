package com.dppl.mycards.card.service;

import java.util.List;

import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.service.dto.CardDTO;
import com.dppl.mycards.card.service.dto.RequestDTO;

public interface CardService {

    Card addCard(RequestDTO requestDTO);

    Card updateCard(RequestDTO requestDTO);

    void deleteCard(RequestDTO requestDTO);

	Card getCard(Long cardId);

	List<Card> getCardsForUser(long long1);

	List<Card> getAllCards();
}
