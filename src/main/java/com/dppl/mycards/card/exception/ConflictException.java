package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4802075541990953880L;

	public ConflictException(String message) {
        super(message);
    }
}