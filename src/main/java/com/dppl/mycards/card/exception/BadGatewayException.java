package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class BadGatewayException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6655686833094579255L;

	private static final String MESSAGE = "Bad Gateway exception occured during # operation.";
	
	public BadGatewayException(String operation) {
        super(MESSAGE.replace("#", operation));
    }
}