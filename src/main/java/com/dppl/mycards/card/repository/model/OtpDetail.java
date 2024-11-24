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
@Table(name="otp_details")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;

    private Long userId;
    
    private Long partnerId;

    private String emailOtp;

    private String mobileOtp;

    private LocalDateTime createdTimestamp;

    private LocalDateTime expiryTimestamp;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime updatedTimestamp;
    
    private String email;
    
    private String mobile;
	
}
