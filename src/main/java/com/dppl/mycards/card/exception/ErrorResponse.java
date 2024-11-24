package com.dppl.mycards.card.exception;

public class ErrorResponse {
	
	// Card errors
	public static final String DUPLICATE_CARD = "Card already exists.";
	public static final String CARD_EXPIRED = "Card expired.";

	// UserActivity errors
	public static final String INVALID_PARAMETER = "Invalid parameter value.";
	
	// User errors
	public static final String DUPLICATE_EMAIL_OR_MOBILE = "Email or mobile already registered.";
	public static final String INVALID_OTP = "Invalid OTP.";
	public static final String OTP_EXPIRED = "OTP expired.";
	public static final String USER_NOT_REGISTERED = "User not registered";
	
}
