package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7445232066615298196L;

	private static final String MESSAGE = "Service: $ is facing too many requests during # operation.";
	
	public TooManyRequestsException(String service, String operation) {
		super(MESSAGE.replace("$", service).replace("#", operation));
    }
}