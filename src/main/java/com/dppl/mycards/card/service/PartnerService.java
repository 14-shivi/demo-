package com.dppl.mycards.card.service;

import java.util.List;
import java.util.Map;

import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.utility.OtpContext;

public interface PartnerService {
	
	Map<String, Object> loginPartner(Map<String, String> requestAttributes);

	String resetPassword(Map<String, String> requestAttributes);

	Partner updateProfile(Map<String, String> requestAttributes);

	Partner signUp(Map<String, String> requestAttributes);

	String generateOtp(Map<String, String> requestAttributes);

	boolean verifyOtp(Map<String, String> requestAttributes);

	PartnerActivity saveActivity(Map<String, String> requestAttributes);

	List<PartnerActivity> getActivityForSpecificPartner(String partnerId, Map<String, String> requestAttributes);

	List<PartnerActivity> getAllActivitiesBasedOnEventType(Map<String, String> requestAttributes);

	String generateOtp(Map<String, String> requestAttributes, OtpContext passwordReset);

}
