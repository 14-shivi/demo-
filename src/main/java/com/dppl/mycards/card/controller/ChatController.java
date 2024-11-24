package com.dppl.mycards.card.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dppl.mycards.card.service.ChatService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.RequestValidator;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	
	private static final Logger LOGGER = LogManager.getLogger(ChatController.class);
	
    private ChatService chatService;
    
    public ChatController(ChatService chatService) {
		super();
		this.chatService = chatService;
	}

	@SuppressWarnings("unchecked")
	@PostMapping
    public ResponseEntity<ResponseDTO<?>> sendMessage(@RequestBody RequestDTO requestDTO) {
    	LOGGER.info("RequestId: {} :: Send-message request recieved.", MDC.get(Keys.REQUEST_ID));
        Map<String, String> requestAttributes = (Map<String, String>) requestDTO.getData().getAttributes();
        new RequestValidator(requestAttributes)
                .hasEmail(Keys.USER_EMAIL)
                .hasMobile(Keys.USER_MOBILE)
                .hasString(Keys.CHAT_INPUT_MESSAGE);
        
        ResponseDTO<?> response = chatService.sendMessage(requestAttributes);
        return ResponseEntity.ok(response);
    }
}
