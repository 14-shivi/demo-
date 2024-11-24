package com.dppl.mycards.card.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.utility.Keys;

public class RequestResponseDataGenerationUtil {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static RequestDTO generateRequestDTO(Map<String, String> attributes) {
		RequestData requestData = new RequestData();
		requestData.setAttributes(attributes);
		   
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setData(requestData);
		   
		return requestDTO;
	}
	
	public static Map<String, String> generateUserRequestAttributes() {
	   	Map<String, String> attributes = new HashMap<>();
	   	attributes.put(Keys.USER_EMAIL, generateRandomEmail());
	   	attributes.put(Keys.USER_MOBILE, generateRandomMobile());
	   	attributes.put(Keys.USER_PASSWORD, "password");
	   	attributes.put(Keys.USER_ACTIVITY_IP_ADDRESS, "192.168.1.1");
	   	attributes.put(Keys.USER_DETAILS_FIRST_NAME, generateRandomName());
	   	attributes.put(Keys.USER_DETAILS_LAST_NAME, generateRandomName());
	   	attributes.put(Keys.USER_DETAILS_TERMS_ACCEPTED, "true");
	   	attributes.put(Keys.USER_DETAILS_GENDER, "male");
	   	attributes.put(Keys.USER_DETAILS_DOB, "1998-05-05");
	   	attributes.put(Keys.USER_DETAILS_SALUTATION, "Mr");
	   	attributes.put(Keys.EMAIL_OTP, "123456");
	   	attributes.put(Keys.MOBILE_OTP, "123456");
	   	
	   	return attributes;
	}
	
	public static User generateUserUsingAttributes(Map<String, String> attributes) {
		
		return User.builder()
				.email(attributes.get(Keys.USER_EMAIL))
				.mobile(attributes.get(Keys.USER_MOBILE))
				.password(attributes.get(Keys.USER_PASSWORD))
				.createdBy("vishwas-test")
				.createdTimestamp(LocalDateTime.now())
				.build();
	}
	
	public static Map<String, String> generateCardRequestAttributes() {
		Map<String, String> attributes = new HashMap<>();
  	
    	attributes.put(Keys.BANK_NAME, "ABC test bank");
    	attributes.put(Keys.CARD_NUMBER, generateRandomCardNumber());
    	attributes.put(Keys.CARD_EXPIRY_DATE, "11/31");
    	attributes.put(Keys.CARD_ISSUE_DATE, "11/23");
    	attributes.put(Keys.CARD_TYPE, "debit");
    	attributes.put(Keys.CARD_NAME, "platinum");
    	
    	return attributes;
	}
	
	public static void generateUserActivityRequestAttributes() {
		
	}
	
	public static String generateRandomEmail() {
        return generateRandomString(8) + ".test@example.com";
    }

	public static String generateRandomMobile() {
	    Random random = new Random();
	    StringBuilder mobile = new StringBuilder(10);   
	    mobile.append(random.nextInt(9) + 1);
	    for (int i = 1; i < 10; i++) {
	        mobile.append(random.nextInt(10));
	    }
	    
	    return mobile.toString();
	}


    public static String generateRandomName() {
        int length = 5 + new Random().nextInt(16);
        return generateRandomString(length);
    }

    public static String generateUUID() {
        Random random = new Random();
        StringBuilder uuid = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            uuid.append(random.nextInt(10));
        }
        return uuid.toString();
    }

    private static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(chars.charAt(random.nextInt(chars.length())));
        }
        return str.toString();
    }
    
    public static String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder(16);
        Random random = new Random();
 
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        return cardNumber.toString();
    }
    
    public static Map<String, Object> generateUserResponseAttributes(User user) {
    	Map<String, Object> map = new HashMap<>();
		map.put(Keys.USER_ID, user.getUserId());
		map.put(Keys.USER_EMAIL, user.getEmail());
		map.put(Keys.USER_MOBILE, user.getMobile());
		return map;
    }

	public static Map<String, String> generatePartnerRequestAttributes() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put(Keys.PARTNER_EMAIL, generateRandomEmail());
		attributes.put(Keys.PARTNER_MOBILE, generateRandomMobile());
		attributes.put(Keys.PARTNER_PASSWORD, "password");
		attributes.put(Keys.EMAIL_OTP, "347017");
		attributes.put(Keys.MOBILE_OTP, "347017");
		attributes.put(Keys.PARTNER_IP_ADDRESS, "192.168.1.1");
		attributes.put(Keys.PARTNER_COMPANY_NAME, "CompanyName");
		attributes.put(Keys.PARTNER_COMPANY_ADDRESS, "Address abc");
		attributes.put(Keys.PARTNER_DATE_OF_INCORPORATION, "1998-05-05");
		attributes.put(Keys.PARTNER_LICENSE, "asdcf12345");
		attributes.put(Keys.PARTNER_CIN, generateUniqueCIN());
		attributes.put(Keys.PARTNER_GST_NUMBER, generateUniqueGstNo());

		return attributes;
	}

	public static String generateUniqueCIN() {
		Random random = new Random();
		StringBuilder cin = new StringBuilder();
		cin.append(random.nextBoolean() ? "L" : "U");

		for (int i = 0; i < 5; i++)
			cin.append(random.nextInt(10));

		String[] stateCodes = {"MH", "DL", "KA", "TN", "GJ"};
		cin.append(stateCodes[random.nextInt(stateCodes.length)]);

		int year = 1980 + random.nextInt(41);
		cin.append(year);

		cin.append("PLC");

		for (int i = 0; i < 6; i++)
			cin.append(random.nextInt(10));

		return cin.toString();
	}

	public static String generateUniqueGstNo() {
		Random random = new Random();
		StringBuilder gstNumber = new StringBuilder();

		int stateCode = 1 + random.nextInt(35);
		gstNumber.append(String.format("%02d", stateCode));

		for (int i = 0; i < 5; i++)
			gstNumber.append((char) ('A' + random.nextInt(26)));

		for (int i = 0; i < 4; i++)
			gstNumber.append(random.nextInt(10));

		gstNumber.append((char) ('A' + random.nextInt(26)));
		gstNumber.append(random.nextInt(9) + 1);
		gstNumber.append("Z");
		gstNumber.append((char) ('A' + random.nextInt(26)));

		return gstNumber.toString();
	}
	
	public static Map<String, Object> generatePartnerResponseAttributes(Partner partner) {
	    Map<String, Object> map = new HashMap<>();
	    map.put(Keys.PARTNER_ID, partner.getId());
	    map.put(Keys.PARTNER_EMAIL, partner.getEmail());
	    map.put(Keys.PARTNER_MOBILE, partner.getMobile());
	    map.put(Keys.PARTNER_COMPANY_NAME, partner.getCompanyName());
	    map.put(Keys.PARTNER_COMPANY_ADDRESS, partner.getCompanyAddress());
	    map.put(Keys.PARTNER_DATE_OF_INCORPORATION, partner.getDateOfIncorporation());
	    map.put(Keys.PARTNER_LICENSE, partner.getLicense());
	    map.put(Keys.PARTNER_CIN, partner.getCin());
	    map.put(Keys.PARTNER_GST_NUMBER, partner.getGstNo());
	    return map;
	}
	
	public static Partner generatePartnerUsingAttributes(Map<String, String> attributes) {
	    return Partner.builder()
	            .email(attributes.get(Keys.PARTNER_EMAIL))
	            .mobile(attributes.get(Keys.PARTNER_MOBILE))
	            .password(attributes.get(Keys.PARTNER_PASSWORD))
	            .companyName(attributes.get(Keys.PARTNER_COMPANY_NAME))
	            .companyAddress(attributes.get(Keys.PARTNER_COMPANY_ADDRESS))
	            .dateOfIncorporation(LocalDate.parse(attributes.get(Keys.PARTNER_DATE_OF_INCORPORATION)))
	            .license(attributes.get(Keys .PARTNER_LICENSE))
	            .cin(attributes.get(Keys.PARTNER_CIN))
	            .gstNo(attributes.get(Keys.PARTNER_GST_NUMBER))
	            .build();
	}

	public static  Map<String, String> generatePartnerActivityRequestAttributes() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put(Keys.PARTNER_ACTIVITY_EVENT_TYPE, Keys.SIGN_IN_ACTIVITY);
		attributes.put(Keys.PARTNER_IP_ADDRESS, "192.168.1.1");
		
		return attributes;
	}


}
