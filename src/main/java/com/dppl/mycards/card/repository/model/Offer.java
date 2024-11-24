package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="offers")
public class Offer {

	@Id
    private String offerId;

    private String description;

    private String type;

    private String redeemType;

    private String startDate;

    private String endDate;

    private String quantity;

    private String quantityType;

    private String offerCode;

    private String conditions;

    private String createdBy;

    private LocalDateTime createdTimestamp;

    private String updatedBy;

    private LocalDateTime updatedTimestamp;
	
}
