package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2795862008536213709L;

	private static final String MESSAGE = "Technical error occured during # operation.";
	
	public InternalServerErrorException(String operation) {
		super(MESSAGE.replace("#", operation));
    }
}