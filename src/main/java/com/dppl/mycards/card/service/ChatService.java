package com.dppl.mycards.card.service;

import java.util.Map;

import com.dppl.mycards.card.service.dto.ResponseDTO;

public interface ChatService {

    ResponseDTO<?> sendMessage(Map<String, String> requestAttributes);
}
