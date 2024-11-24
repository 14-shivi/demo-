package com.dppl.mycards.card.service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetaDTO<T> {
	
	private T attributes;
	
}
