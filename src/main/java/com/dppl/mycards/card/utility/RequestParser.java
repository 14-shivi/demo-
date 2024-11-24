package com.dppl.mycards.card.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;

import com.dppl.mycards.card.repository.model.Card;
import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.PartnerActivity;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.repository.model.UserDetails;
import com.dppl.mycards.card.service.dto.RequestDTO;

public class RequestParser {
	private static final Logger LOGGER = LogManager.getLogger();

	public UserDetails parseUserDetail(Map<String, String> attributes) {
		LOGGER.info("RequestID: {} :: Parsing UserDetails.", MDC.get(Keys.REQUEST_ID));
		return UserDetails.builder()
				.salutation(attributes.get(Keys.USER_DETAILS_SALUTATION))
				.firstName(attributes.get(Keys.USER_DETAILS_FIRST_NAME))
				.lastName(attributes.get(Keys.USER_DETAILS_LAST_NAME))
				.gender(attributes.get(Keys.USER_DETAILS_GENDER))
				.dob(attributes.get(Keys.USER_DETAILS_DOB))
				.termsAccepted(attributes.get(Keys.USER_DETAILS_TERMS_ACCEPTED))
				.build();
		
	}
	
	public User parseUser(Object object) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> userAttributes = (Map<String, String>) object;

		return User.builder()
				.mobile(userAttributes.get(Keys.USER_MOBILE))
				.email(userAttributes.get(Keys.USER_EMAIL))
				.password(userAttributes.get(Keys.USER_PASSWORD))
				.createdBy(userAttributes.get(Keys.USER_CREATED_BY))
				.updatedBy(userAttributes.get(Keys.USER_UPDATED_BY))
				.build();
	}
	
	public UserActivity parseUserActivity(Object object) {
		LOGGER.info("RequestID: {} :: Parsing UserActivity.", MDC.get(Keys.REQUEST_ID));
		
		@SuppressWarnings("unchecked")
		Map<String, String> attributes= (Map<String, String>) object;
		
		Long userId = attributes.get(Keys.USER_ID) == null ? null : Long.parseLong(attributes.get(Keys.USER_ID));
		
		return UserActivity.builder()				
				.ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
				.userId(userId)
				.eventType(attributes.get(Keys.USER_ACTIVITY_EVENT_TYPE))
				.eventDetails(attributes.get(Keys.USER_ACTIVITY_EVENT_DETAILS))
				.eventTimestamp(LocalDateTime.now())
				.build();
	}

	public Card parseCard(Object object) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> attributes = (Map<String, String>) object;
		
		return Card.builder()
				.bankName(attributes.get(Keys.BANK_NAME))
				.cardNumber(attributes.get(Keys.CARD_NUMBER))
				.expiry(attributes.get(Keys.CARD_EXPIRY_DATE))
				.issueDate(attributes.get(Keys.CARD_ISSUE_DATE))
				.cardType(attributes.get(Keys.CARD_TYPE))
				.cardName(attributes.get(Keys.CARD_NAME))
				.build();
	}
	
	public UserActivity parseUserActivity(Map<String, String> attributes, String eventType) {
		
		return UserActivity.builder()
				.eventType(eventType)
				.build();
	}

	public UserActivity parseUserActivity(RequestDTO requestDTO) {
		@SuppressWarnings("unchecked")
		Map<String, String> attributes = (Map<String, String>) requestDTO.getData().getAttributes();
		
		return UserActivity.builder()
				.eventType(attributes.get("eventType"))
				.build();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> parseAttributes(RequestDTO requestDTO) {
		
		return (Map<String, String>) requestDTO.getData().getAttributes();
	}

	public Partner parsePartner(Map<String, String> requestAttributes) {
		LOGGER.info("RequestID: {} :: Parsing Partner.", MDC.get(Keys.REQUEST_ID));
		String dateOfIncorpString = requestAttributes.get(Keys.PARTNER_DATE_OF_INCORPORATION);
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//		LocalDate dateofIncorp = dateOfIncorpString == null ?
//				null : LocalDate.parse(dateOfIncorpString, formatter);
		LocalDate dateofIncorp = dateOfIncorpString == null ?
				null : LocalDate.parse(dateOfIncorpString);
		
		String partnerIdString = requestAttributes.get(Keys.PARTNER_ID);
		Long partnerId = partnerIdString == null ? null : Long.parseLong(partnerIdString);
		
		return Partner.builder()
				.id(partnerId)
				.email(requestAttributes.get(Keys.PARTNER_EMAIL))
				.mobile(requestAttributes.get(Keys.PARTNER_MOBILE))
				.password(requestAttributes.get(Keys.PARTNER_PASSWORD))
				.ipAddress(requestAttributes.get(Keys.PARTNER_IP_ADDRESS))
				.companyName(requestAttributes.get(Keys.PARTNER_COMPANY_NAME))
				.companyAddress(requestAttributes.get(Keys.PARTNER_COMPANY_ADDRESS))
				.dateOfIncorporation(dateofIncorp)
				.license(requestAttributes.get(Keys.PARTNER_LICENSE))
				.cin(requestAttributes.get(Keys.PARTNER_CIN))
				.gstNo(requestAttributes.get(Keys.PARTNER_GST_NUMBER))
				.build();
	}

	public PartnerActivity parsePartnerActivity(Map<String, String> attributes) {
		LOGGER.info("RequestID: {} :: Parsing PartnerActivity.", MDC.get(Keys.REQUEST_ID));
	
		Long partnerId = attributes.get(Keys.PARTNER_ID) == null ? 
				null : Long.parseLong(attributes.get(Keys.PARTNER_ID));
		
		return PartnerActivity.builder()				
				.ipAddress(attributes.get(Keys.USER_ACTIVITY_IP_ADDRESS))
				.eventType(attributes.get(Keys.USER_ACTIVITY_EVENT_TYPE))
				.eventDetails(attributes.get(Keys.PARTNER_ACTIVITY_EVENT_DETAILS))
				.partnerId(partnerId)
				.eventTimestamp(LocalDateTime.now())
				.build();
	}
	
	
}
