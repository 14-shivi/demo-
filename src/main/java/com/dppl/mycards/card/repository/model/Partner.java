package com.dppl.mycards.card.repository.model;

import java.time.LocalDate;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name="partners")
public class Partner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false, unique = true)
	private String mobile;
	
	@Column(nullable = false)
	private String password;
	
	@Column
	private String ipAddress;
	
	@Column
	private String companyName;
	
	@Column
	private String companyAddress;
	
	@Column
	private LocalDate dateOfIncorporation;
	
	@Column
	private String license;
	
	@Column(unique = true)
	private String cin;         // Company ID Number
	
	@Column(unique = true)
	private String gstNo;
	
}
