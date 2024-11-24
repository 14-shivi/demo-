package com.dppl.mycards.card.service;

import java.util.List;

import com.dppl.mycards.card.repository.UserActivityRepository;
import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.service.dto.RequestDTO;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

public interface UserActivityService {
	
	public UserActivity saveUserActivity(RequestDTO requestDTO, String activityType);

	public UserActivity saveUserActivity(Long userId, String eventType, String eventDetails, RequestDTO requestDTO);

	public List<UserActivity> getAllUserActivitiesBasedOnEventType(String eventType);

	public List<UserActivity> getUserActivityForSpecificUser(String param, RequestDTO requestDTO);



	List<UserActivity> getAllUserActivities();
}



