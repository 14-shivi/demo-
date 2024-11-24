package com.dppl.mycards.card.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cardId;
	private String bankName;
	@Column(name = "card_number", unique = true)
	private String cardNumber;
	private String expiry;
	private String issueDate;
	private String cardType; // debit/credit
	private String cardName; // platinum/moneyback/simplySave etc.

}
