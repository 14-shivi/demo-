package com.dppl.mycards.card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1563546506003657938L;

	private static final String MESSAGE = "Service: $ was found unavailable during # operation.";
	
	public ServiceUnavailableException(String service, String operation) {
		super(MESSAGE.replace("$", service).replace("#", operation));
    }
}
