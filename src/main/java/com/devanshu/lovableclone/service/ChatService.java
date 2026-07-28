package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.chat.ChatHistoryResponseDTO;

public interface ChatService {
	ChatHistoryResponseDTO getProjectChatHistory(Long projectId);
}
