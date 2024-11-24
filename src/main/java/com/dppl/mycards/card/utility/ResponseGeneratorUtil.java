package com.dppl.mycards.card.utility;

import java.util.Map;
import java.util.UUID;

import com.dppl.mycards.card.service.dto.DataDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;

public class ResponseGeneratorUtil {
	
	private ResponseGeneratorUtil() {
		super();
	}

	public static ResponseDTO<Object> generateResponseDTO(String responseType, String key, Object value) {
		
		return ResponseDTO.builder()
    			.data(new DataDTO<>(UUID.randomUUID().toString(), responseType, Map.of(key, value)))
    			.build();
	}
	
}
