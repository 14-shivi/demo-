package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_cards")
public class UserCard {

	@Id
    private String userCardId;

    private String userId;

    private String cardId;

    private String cardNumber;

    private String expiry;

    private String bankName;

    private String issueDate;

    private String cardType;

    private String cardName;

    private String createdBy;

    private LocalDateTime createdTimestamp;

    private String updatedBy;

    private LocalDateTime updatedTimestamp;
	
}
