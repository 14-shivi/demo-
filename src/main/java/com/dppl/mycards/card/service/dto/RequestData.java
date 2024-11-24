package com.dppl.mycards.card.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestData<T> {

	private String id;
	private String type;
	private T attributes;

}
