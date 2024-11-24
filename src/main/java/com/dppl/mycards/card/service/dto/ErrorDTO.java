package com.dppl.mycards.card.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorDTO {

	private String id;
    private String code;
    private String title;
    private String detail;
	
}
