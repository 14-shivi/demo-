package com.dppl.mycards.card.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO<T> {

	private String id;
    private String type;
    private T attributes;
	
}
