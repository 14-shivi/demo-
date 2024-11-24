package com.dppl.mycards.card.service.real_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dppl.mycards.card.repository.UserActivityRepository;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.impl.UserActivityServiceImpl;
import com.dppl.mycards.card.utility.Keys;

@SpringBootTest
class UserActivityServiceImplRealTest {
	
	@Autowired
	private UserActivityServiceImpl activityService;
	
	@Autowired
	private UserActivityRepository activityRepository;
	
	@Test
	void saveUserActivityTest() {
		RequestDTO requestDTO = requestBuilderHelperMethod();
		UserActivity userActivity = activityService.saveUserActivity(requestDTO, null);
		
		@SuppressWarnings("unchecked")
		Map<String, String> atributes = (Map<String, String>) requestDTO.getData().getAttributes();
		
		assertNotNull(userActivity);
		assertEquals(atributes.get(Keys.USER_ACTIVITY_EVENT_TYPE), 
				userActivity.getEventType());
		assertNotNull(activityRepository.findById(userActivity.getActivityId()));
		
	}
	
	RequestDTO requestBuilderHelperMethod() {
		
		Map<String, String> attributes = new HashMap<>();
    	
    	String email = UUID.randomUUID().toString();
    	String mobile = UUID.randomUUID().toString();
    	
    	attributes.put(Keys.USER_ID, "10");
    	attributes.put(Keys.USER_EMAIL, email);
    	attributes.put(Keys.USER_MOBILE, mobile);
    	attributes.put(Keys.USER_ACTIVITY_EVENT_TYPE, "Sign-in");
		
    	RequestDTO requestDTO = new RequestDTO();
    	requestDTO.setData(new RequestData<Map<String, String>>("1212", "user", attributes));
    	
    	return requestDTO;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void getUserActivityForSpecificUserByUserIdTest() {
	    RequestDTO requestDTO = requestBuilderHelperMethod();
		UserActivity userActivity = activityService.saveUserActivity(requestDTO, null);
		
		Map<String, String> attributes = (Map<String, String>) requestDTO.getData().getAttributes();
		attributes.put(Keys.USER_ID, userActivity.getUserId().toString());
	    
		requestDTO.setData(new RequestData<Map<String, String>>("", "", attributes));
		
	    List<UserActivity> userActivities = activityService.getUserActivityForSpecificUser("userId", requestDTO);

	    assertNotNull(userActivities);
	}

	@Test
	void getUserActivityForSpecificUserByEmailTest() {
	    RequestDTO requestDTO = requestBuilderHelperMethod();
	    List<UserActivity> userActivities = activityService.getUserActivityForSpecificUser("emailId", requestDTO);

	    assertNotNull(userActivities);
	    
	}

	@Test
	void getUserActivityForSpecificUserByMobileTest() {
	    RequestDTO requestDTO = requestBuilderHelperMethod();
	    List<UserActivity> userActivities = activityService.getUserActivityForSpecificUser("mobile", requestDTO);

	    assertNotNull(userActivities);
	}

	@Test
	void getAllUserActivitiesBasedOnEventTypeTest() {
	    String eventType = "Sign-in";
	    List<UserActivity> userActivities = activityService.getAllUserActivitiesBasedOnEventType(eventType);

	    assertNotNull(userActivities);
	    assertFalse(userActivities.isEmpty());
	    assertEquals(eventType, userActivities.get(0).getEventType());
	}

	
	
	
}
