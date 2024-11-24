package com.dppl.mycards.card.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LargeLanguageModelUtil {
	
	private WebClient webClient;
	
	@Value("${llm.api.key}")
	private String apiKey;
	
	@Value("${llm.api.url}")
	private String llmUrl;
	
	private static final Logger LOGGER = LogManager.getLogger(LargeLanguageModelUtil.class);
	
	public LargeLanguageModelUtil(WebClient webClient) {
		super();
		this.webClient = webClient;
	}

	public Object generateResponseUsingPrompt(String prompt) {
		LOGGER.info("RequestId: {} :: Sending prompt to LLM.", MDC.get(Keys.REQUEST_ID));
		String requestBody = """
                {
                    "contents": [{
                        "parts":[{"text": "%s"}]
                    }]
                }
			    """.formatted(prompt);
		
		String response = webClient.post()
		.uri(llmUrl + apiKey)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(requestBody)
		.retrieve()
		.bodyToMono(String.class)
		.block();
		
		
		
		return formatJson(response);
	}
	
	private Object formatJson(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, Object.class);
        } catch (Exception e) {
            
        	throw new RuntimeException("Error occured while converting jsonResponse to the object");
        }
    }
	
}
