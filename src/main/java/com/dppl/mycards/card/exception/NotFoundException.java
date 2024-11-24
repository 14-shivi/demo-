package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8601627533606425865L;

	private static final String MESSAGE = "$ not found during # operation.";
	
	public NotFoundException(String entity, String operation) {
		super(MESSAGE.replace("$", entity).replace("#", operation));
	}
}