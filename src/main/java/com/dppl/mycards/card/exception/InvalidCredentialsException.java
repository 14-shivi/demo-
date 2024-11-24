package com.dppl.mycards.card.exception;

public class InvalidCredentialsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1201470428673590519L;
	
	private static final String MESSAGE = "Invalid $ found during # operation.";

	public InvalidCredentialsException(String credential, String operation) {
		super(MESSAGE.replace("$", credential).replace("#", operation));
	}

}
