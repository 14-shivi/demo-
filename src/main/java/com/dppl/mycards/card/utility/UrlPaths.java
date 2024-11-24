package com.dppl.mycards.card.utility;

public class UrlPaths {

	// Base URL
	public static final String BASE_URL = "/api";
	
	// Partners API Base-url
	public static final String BASE_URL_PARTNERS_API = BASE_URL + "/partners/banks";
	
	// OTP generation URLs
	public static final String GENERATE_OTP_REGISTRATION = BASE_URL_PARTNERS_API + "/otp/generate-register";
	public static final String GENERATE_OTP_LOGIN = BASE_URL_PARTNERS_API + "/otp/generate-login";
	public static final String GENERATE_OTP_PASSWORD_RESET = BASE_URL_PARTNERS_API + "/otp/generate-reset";
	public static final String GENERATE_OTP_UPDATE_PROFILE = BASE_URL_PARTNERS_API + "";
	public static final String GENERATE_OTP_NO_CONTEXT = BASE_URL_PARTNERS_API + "/otp/generate";

	
}
