package com.dppl.mycards.card.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import com.dppl.mycards.card.exception.NotFoundException;
import com.dppl.mycards.card.repository.ChatHistoryRepository;
import com.dppl.mycards.card.repository.UserRepository;
import com.dppl.mycards.card.repository.model.ChatHistory;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.ChatService;
import com.dppl.mycards.card.service.dto.ResponseDTO;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.LargeLanguageModelUtil;
import com.dppl.mycards.card.utility.ResponseGeneratorUtil;

@Service
public class ChatServiceImpl implements ChatService {

	private static final Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);
	
	private final LargeLanguageModelUtil llmUtil;
    private final UserRepository userRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    public ChatServiceImpl(LargeLanguageModelUtil llmUtil, 
                     UserRepository userRepository, 
                     ChatHistoryRepository chatHistoryRepository) {
        this.llmUtil = llmUtil;
        this.userRepository = userRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }
	
	@Override
	public ResponseDTO<?> sendMessage(Map<String, String> requestAttributes) {
    	LOGGER.info("RequestId: {} :: Calling llm-service.", MDC.get(Keys.REQUEST_ID)); 	
    	Optional<User> userOptional;
    	userOptional = userRepository.findByEmail(requestAttributes.get(Keys.USER_EMAIL));
    	
    	if (userOptional.isEmpty())
    		userOptional = userRepository.findByMobile(requestAttributes.get(Keys.USER_MOBILE));
    	if (userOptional.isEmpty())
    		throw new NotFoundException(Keys.ENTITY_TYPE_CHAT, "sendMessage");	
    	
    	String inputMessage = requestAttributes.get(Keys.CHAT_INPUT_MESSAGE);
		Object llmResponse = llmUtil.generateResponseUsingPrompt(inputMessage);
    	
    	ChatHistory chatHistory = ChatHistory.builder()
    			.inputMessage(inputMessage)
    			.outputMessage(llmResponse.toString())
    			.createdBy("vishwas-dev")
    			.createdTimestamp(LocalDateTime.now())
    			.userId(userOptional.get().getUserId())
    			.build();
    	
    	chatHistoryRepository.save(chatHistory);
    		
		return ResponseGeneratorUtil.generateResponseDTO(Keys.RESPONSE_TYPE_CHAT, Keys.CHAT_OUTPUT_MESSAGE, llmResponse);
	}
}
