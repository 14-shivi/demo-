package com.dppl.mycards.card.exception;

public class DuplicateResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4293790731171700070L;

	private static final String MESSAGE = "Duplicate $ found during # operation.";
	
	public DuplicateResourceException(String entity, String operation) {
		super(MESSAGE.replace("$", entity).replace("#", operation));
	}
	
}
