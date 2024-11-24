package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "users")
@Schema(description = "User Entity")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	
	@Column(name = "password", nullable = false)
	private String password; // In a real application, ensure passwords are encrypted
	
	@Column(name = "mobile", nullable = true, unique = false, updatable = true)
	private String mobile;
	
	@Column(name = "email", nullable = true, unique = false, updatable = true)
	private String email;
	
	@Column(name = "status", nullable = true, unique = false, updatable = true)
	private String status;
	
	@Column(name = "createdBy", nullable = true, unique = false, updatable = true)
	private String createdBy;
	
	@Column(name = "created_timestamp", nullable = true, unique = false, updatable = true)
	private LocalDateTime createdTimestamp;
	
	@Column(name = "updatedBy", nullable = true, unique = false, updatable = true)
	private String updatedBy;
	
	@Column(name = "updated_timestamp", nullable = true, unique = false, updatable = true)
	private LocalDateTime updatedTimestamp;
	
}
