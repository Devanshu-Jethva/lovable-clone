package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.chat.ChatResponseDTO;

import java.util.List;

public interface ChatService {
    List<ChatResponseDTO> getProjectChatHistory(Long projectId);
}
