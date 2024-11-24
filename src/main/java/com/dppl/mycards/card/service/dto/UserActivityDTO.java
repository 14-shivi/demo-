package com.dppl.mycards.card.service.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.dppl.mycards.card.repository.model.UserActivity;

import lombok.Data;

@Data
public class UserActivityDTO {

	private Long id;

	private String mobile;

	private String email;

	private String eventType;

	private LocalDateTime timeStamp;

}
