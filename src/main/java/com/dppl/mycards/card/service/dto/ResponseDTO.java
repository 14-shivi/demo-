package com.dppl.mycards.card.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
//	private String id;
//	private String type;
    private DataDTO<T> data;
    private MetaDTO<T> meta;
    private List<ErrorDTO> errors;
}
