package com.dppl.mycards.card.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import com.dppl.mycards.card.exception.BadRequestException;
import com.dppl.mycards.card.exception.ErrorResponse;
import com.dppl.mycards.card.repository.UserActivityRepository;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.service.UserActivityService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.RequestParser;

@Service
public abstract class UserActivityServiceImpl implements UserActivityService{
	
private static final Logger LOGGER = LogManager.getLogger();
	
	UserActivityRepository userActivityRepository;
	
	public UserActivityServiceImpl(UserActivityRepository userActivityRepository) {
		super();
		this.userActivityRepository = userActivityRepository;
	}
	
	public UserActivity saveUserActivity(RequestDTO requestDTO, String activityType) {
		LOGGER.info("RequestId: {} :: Saving UserActivity", MDC.get(Keys.REQUEST_ID));
		
		UserActivity userActivity = new RequestParser().parseUserActivity(requestDTO.getData().getAttributes());
		return userActivityRepository.save(userActivity);
	}

	@Override
	public UserActivity saveUserActivity(Long userId, String eventType, String eventDetails, RequestDTO requestDTO) {
		LOGGER.info("RequestId: {} :: Saving UserActivity", MDC.get(Keys.REQUEST_ID));
		
		UserActivity userActivity = new RequestParser().parseUserActivity(requestDTO.getData().getAttributes());
		
		userActivity.setUserId(userId);
		userActivity.setEventType(eventType);
		userActivity.setEventTimestamp(LocalDateTime.now());
		userActivity.setEventDetails(eventDetails);
		
		return userActivityRepository.save(userActivity);	
	}

	@Override
	public List<UserActivity> getUserActivityForSpecificUser(String param, RequestDTO requestDTO) {
	    LOGGER.info("RequestId: {} :: Finding user activity from DB for a specific user", MDC.get(Keys.REQUEST_ID));

	    @SuppressWarnings("unchecked")
		Map<String, String> attributes =  (Map<String, String>) requestDTO.getData().getAttributes();
	    
	    String startDateString = attributes.get("startDate");
	    LocalDateTime startDateTime = startDateString != null 
	        ? LocalDate.parse(startDateString).atStartOfDay() 
	        : LocalDateTime.of(1970, 1, 1, 0, 0);

	    String endDateString = attributes.get("endDate");
	    LocalDateTime endDateTime = endDateString != null 
	        ? LocalDate.parse(endDateString).atTime(23, 59, 59) 
	        : LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

	    String eventType = attributes.get("eventType");
	    String email = attributes.get("email");
	    String mobile = attributes.get("mobile");

	    return switch (param) {
	        case "userId" -> userActivityRepository.findByCriteria(startDateTime, endDateTime, eventType, email, mobile);
	        case "emailId" -> userActivityRepository.findByCriteria(startDateTime, endDateTime, eventType, email, null);
	        case "mobile" -> userActivityRepository.findByCriteria(startDateTime, endDateTime, eventType, null, mobile);
	        default -> throw new BadRequestException(ErrorResponse.INVALID_PARAMETER);
	    };
	}


//	private List<UserActivity> findUserActivityByUserId(RequestDTO requestDTO) {
//
//		String email = requestDTO.getData().getAttributes().get("email");
//		String mobile = requestDTO.getData().getAttributes().get("mobile");
//		
//		if (email != null)
//			return userActivityRepository.findByEmail(email);
//		
//		return userActivityRepository.findByMobile(mobile);
//	}

	@Override
	public List<UserActivity> getAllUserActivitiesBasedOnEventType(String eventType) {
		LOGGER.info("RequestId: {} :: Finding user activity from DB based on event", MDC.get(Keys.REQUEST_ID));
		return userActivityRepository.findByEventType(eventType);
	}

	@Override
	public List<UserActivity> getAllUserActivities() {

		return userActivityRepository.findAll();
	}
}
