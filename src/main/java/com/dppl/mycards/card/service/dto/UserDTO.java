package com.dppl.mycards.card.service.dto;

import lombok.Data;

@Data
public class UserDTO {

	private Long id;

	private String username;
	
	private String password;
	
	private String mobile;
	
	private String email;
	
	private String emailOtp;
	
	private String mobileOtp;
	
	private String ipAddress;
	
	private boolean termsAndConditionsAcceptedFlag;
	
	public void parseUserActivity() {
		
	}
	
}
