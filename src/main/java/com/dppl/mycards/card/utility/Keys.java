package com.dppl.mycards.card.utility;

public class Keys {
	
	// User entity keys
	public static final String USER_ID = "userId";
	public static final String USER_PASSWORD = "password";
	public static final String USER_MOBILE = "mobile";
	public static final String USER_EMAIL = "email";
	public static final String USER_STATUS = "status";
	public static final String USER_STATUS_ACTIVE = "active";
	public static final String USER_STATUS_INACTIVE = "inactive";
	public static final String USER_STATUS_DISABLED = "disabled";
	public static final String USER_CREATED_BY = "createdBy";
	public static final String USER_UPDATED_BY = "updatedBy";
	public static final String USER_CREATED_TIMESTAMP = "created_timestamp";
	public static final String USER_UPDATED_TIMESTAMP = "updated_timestamp";
	
	// UserDetails entity keys
	public static final String USER_DETAILS_USER_ID = "userId";
	public static final String USER_DETAILS_SALUTATION = "salutation";
	public static final String USER_DETAILS_FIRST_NAME = "firstName";
	public static final String USER_DETAILS_LAST_NAME = "lastName";
	public static final String USER_DETAILS_GENDER = "gender";
	public static final String USER_DETAILS_DOB = "dob";
	public static final String USER_DETAILS_TERMS_ACCEPTED = "termsAccepted";
	public static final String USER_DETAILS_TERMS_ACCEPTED_TIMESTAMP = "termsAcceptedTimestamp";
	public static final String USER_DETAILS_CREATED_BY = "createdBy";
	public static final String USER_DETAILS_CREATED_TIMESTAMP = "createdTimestamp";
	public static final String USER_DETAILS_UPDATED_BY = "updatedBy";
	public static final String USER_DETAILS_UPDATED_TIMESTAMP = "updatedTimestamp";
	
	// UserActivity entity keys
	public static final String USER_ACTIVITY_ACTIVITY_ID = "activityId";
	public static final String USER_ACTIVITY_USER_ID = "userId";
	public static final String USER_ACTIVITY_EVENT_TYPE = "eventType";
	public static final String USER_ACTIVITY_EVENT_TIMESTAMP = "eventTimestamp";
	public static final String USER_ACTIVITY_IP_ADDRESS = "ipAddress";
	public static final String USER_ACTIVITY_EVENT_DETAILS = "eventDetails";
	
	// UserActivity searching keys
	public static final String USER_ACTIVITY_START_DATE = "startDate";
	public static final String USER_ACTIVITY_END_DATE = "endDate";
	
	// Activity keys
	public static final String ACCOUNT_CREATED_ACTIVITY = "Account created";
	public static final String SIGN_IN_ACTIVITY = "Sign-in";
	public static final String UPDATE_PROFILE_ACTIVITY = "Updated profile";
	public static final String SIGN_OUT_ACTIVITY = "Sign-out";
	public static final String OTP_GENERATED_ACTIVITY = "Generated OTP";
	public static final String OTP_VERIFIED_ACTIVITY = "Verified OTP";
	public static final String PASSWORD_RESET_ACTIVITY = "Password reset";

	
	// Card entity keys
	public static final String CARD_ID = "cardId";
	public static final String BANK_NAME = "bankName";
	public static final String CARD_NUMBER = "cardNumber";
	public static final String CARD_EXPIRY_DATE = "expiry";
	public static final String CARD_ISSUE_DATE = "issueDate";
	public static final String CARD_TYPE = "cardType";
	public static final String CARD_NAME = "cardName";
	
	// RequestID
	public static final String REQUEST_ID = "requestId";
	
	// OTP
	public static final String MOBILE_OTP = "mobileOtp";
	public static final String EMAIL_OTP = "emailOtp";

	// Chat Keys
	public static final String CHAT_INPUT_MESSAGE = "inputMessage";
	public static final String CHAT_OUTPUT_MESSAGE = "outputMessage";
	
	// Response Type Keys
	public static final String RESPONSE_TYPE_USER = "user";
	public static final String RESPONSE_TYPE_CARD = "card";
	public static final String RESPONSE_TYPE_USER_ACTIVITY = "userActivity";
	public static final String RESPONSE_TYPE_CHAT = "chat";
	public static final String RESPONSE_TYPE_PARTNER = "partner";
	public static final String RESPONSE_TYPE_PARTNER_ACTIVITY = "partnerActivity";
	
	// Entity-Name Keys
	public static final String ENTITY_TYPE_USER = "User";
	public static final String ENTITY_TYPE_CARD = "Card";
	public static final String ENTITY_TYPE_USER_ACTIVITY = "User Activity";
	public static final String ENTITY_TYPE_CHAT = "Chat";
	public static final String ENTITY_TYPE_USER_DETAILS = "User Details";
	public static final String ENTITY_TYPE_PARTNER = "Partner";


	// Partner Entity Keys
	public static final String PARTNER_ID = "id";
	public static final String PARTNER_PASSWORD = "password";
	public static final String PARTNER_IP_ADDRESS = "ipAddress";
	public static final String PARTNER_EMAIL_OTP = "emailOtp";
	public static final String PARTNER_MOBILE_OTP = "mobileOtp";
	public static final String PARTNER_EMAIL = "email";
	public static final String PARTNER_MOBILE = "mobile";
	public static final String PARTNER_COMPANY_NAME = "companyName";
	public static final String PARTNER_COMPANY_ADDRESS = "companyAddress";
	public static final String PARTNER_DATE_OF_INCORPORATION = "dateOfIncorporation";
	public static final String PARTNER_LICENSE = "license";
	public static final String PARTNER_CIN = "cin";  // Company ID number
	public static final String PARTNER_GST_NUMBER = "gstNo";

	// Partner Activity Keys
	public static final String PARTNER_ACTIVITY_ID = "activityId";
	public static final String PARTNER_ACTIVITY_EVENT_TYPE = "eventType";
	public static final String PARTNER_ACTIVITY_EVENT_DETAILS = "eventDetails";
}
