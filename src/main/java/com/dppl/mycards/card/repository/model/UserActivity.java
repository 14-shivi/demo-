package com.dppl.mycards.card.repository.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    private Long userId;

    private String eventType;

    private LocalDateTime eventTimestamp;

    private String ipAddress;

    private String eventDetails;
	
}
