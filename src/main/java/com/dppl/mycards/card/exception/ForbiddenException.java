package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8008999967558863061L;

	public ForbiddenException(String message) {
        super(message);
    }
}