package com.dppl.mycards.card.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dppl.mycards.card.repository.model.UserActivity;
import com.dppl.mycards.card.service.UserActivityService;
import com.dppl.mycards.card.service.dto.DataDTO;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.RequestParser;
import com.dppl.mycards.card.utility.RequestValidator;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("api/activity")
@Tag(name = "UserActivity-Controller", description = "CRUD operations for UserActivity")
public class UserActivityController {

	private static final Logger LOGGER = LogManager.getLogger();

    private UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
		super();
		this.userActivityService = userActivityService;
	}

	@PostMapping
    public ResponseEntity<ResponseDTO<Object>> createUserActivity(@RequestBody RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: UserActivity creation request received", MDC.get(Keys.REQUEST_ID));
    	new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasString(Keys.USER_ACTIVITY_EVENT_TYPE)
    	.hasLong(Keys.USER_ID)
    	.hasString(Keys.USER_ACTIVITY_IP_ADDRESS)
    	.hasString(Keys.USER_ACTIVITY_EVENT_DETAILS);
    	
        UserActivity userActivity = userActivityService.saveUserActivity(requestDTO, null);

        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER_ACTIVITY, userActivity))
                .build();

        return ResponseEntity.ok(response);
    }

	@PostMapping("/all")
	public ResponseEntity<ResponseDTO<List<UserActivity>>> getAllUserActivitiesBasedOnEventType(
	        @RequestBody RequestDTO requestDTO) {
	    LOGGER.info("RequestId: {} :: getAllUserActivitiesBasedOnEventType request received", MDC.get(Keys.REQUEST_ID));
	    new RequestValidator(new RequestParser().parseAttributes(requestDTO))
    	.hasString(Keys.USER_ACTIVITY_EVENT_TYPE);

	    @SuppressWarnings("unchecked")
	    Map<String, String> requestMap = (Map<String, String>) requestDTO.getData().getAttributes();

	    List<UserActivity> userActivities = userActivityService.getAllUserActivitiesBasedOnEventType(
	            requestMap.get(Keys.USER_ACTIVITY_EVENT_TYPE));

	    ResponseDTO<List<UserActivity>> response = ResponseDTO.<List<UserActivity>>builder()
	            .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER_ACTIVITY, userActivities))
	            .build();

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/all")
	public ResponseEntity<ResponseDTO<List<UserActivity>>> retrieveAllUserActivities() {
		String requestId = (String) MDC.get(Keys.REQUEST_ID);
		LOGGER.info("RequestId: {} :: retrieveAllUserActivities request received", requestId);

		List<UserActivity> userActivities = userActivityService.getAllUserActivities();

		ResponseDTO<List<UserActivity>> response = ResponseDTO.<List<UserActivity>>builder()
				.data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER_ACTIVITY, userActivities))
				.build();

		return ResponseEntity.ok(response);
	}




	@PostMapping("/{param}")
    public ResponseEntity<?> getUserActivityForSpecificUser( @PathVariable("param") String param,
            @RequestBody(required = false) RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: getUserActivityForSpecificUser request received", MDC.get(Keys.REQUEST_ID));
    	new RequestValidator(new RequestParser().parseAttributes(requestDTO))
//    	.hasDate(Keys.USER_ACTIVITY_START_DATE)
//    	.hasDate(Keys.USER_ACTIVITY_END_DATE)
    	.hasEmail(Keys.USER_EMAIL)
    	.hasMobile(Keys.USER_MOBILE);
    	
    	List<UserActivity> userActivities = userActivityService.getUserActivityForSpecificUser(param, requestDTO);

        ResponseDTO<Object> response = ResponseDTO.builder()
                .data(new DataDTO<>(UUID.randomUUID().toString(), Keys.RESPONSE_TYPE_USER_ACTIVITY, userActivities))
                .build();

        return ResponseEntity.ok(response);
    }

}
