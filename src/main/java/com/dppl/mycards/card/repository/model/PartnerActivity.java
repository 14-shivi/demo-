package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="partner_activities")
public class PartnerActivity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityId;
	
	@Column
	private Long partnerId;
	
	@Column
	private String eventType;
	
	@Column
	private String ipAddress;
	
	@Column
	private LocalDateTime eventTimestamp;
	
	@Column
	private String eventDetails;
	
}
