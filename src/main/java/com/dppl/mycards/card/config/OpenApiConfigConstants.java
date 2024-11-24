package com.dppl.mycards.card.config;

public final class OpenApiConfigConstants {

	private OpenApiConfigConstants() {
		super();
	}
	
	public static final String USER_LOGIN_REQUEST= """
		{
		  "data": {
		    "id": "13453",
		    "type": "user",
		    "attributes": {
		        "email": "vishwasransingh@gmail.com",
		        "mobile": "7743898263",
		        "password": "password",
		        "emailOtp": "123456",
		        "mobileOtp": "654321",
		        "ipAddress": "192.168.1.1"
		    }
		  }
		}
		""";
	
}
