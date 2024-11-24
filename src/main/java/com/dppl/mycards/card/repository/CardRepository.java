package com.dppl.mycards.card.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dppl.mycards.card.repository.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

	Card findByCardNumber(String cardNumber);

	boolean existsByCardNumber(String cardNumber);

	void deleteByCardNumber(String cardNumber);

//	List<Card> findAllByUserId();

}
