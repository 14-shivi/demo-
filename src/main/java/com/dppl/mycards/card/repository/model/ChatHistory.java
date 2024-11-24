package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatId;
	
	private Long userId;
	
	@Column(length = 1000)
	private String inputMessage;
	
	@Column(length = 1000)
	private String outputMessage;
	
	private String createdBy;
	
	private String updatedBy;
	
	private LocalDateTime createdTimestamp;
	
	private LocalDateTime updatedTimestamp;
	
}
