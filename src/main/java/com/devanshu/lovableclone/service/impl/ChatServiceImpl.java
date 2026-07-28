package com.devanshu.lovableclone.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.dto.chat.ChatHistoryResponseDTO;
import com.devanshu.lovableclone.entity.ChatMessage;
import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.entity.ChatSessionId;
import com.devanshu.lovableclone.mapper.ChatMapper;
import com.devanshu.lovableclone.repository.ChatMessageRepository;
import com.devanshu.lovableclone.repository.ChatSessionRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.ChatService;
import com.devanshu.lovableclone.service.HelperService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

	HelperService helperService;
	ChatSessionRepository chatSessionRepository;
	ChatMessageRepository chatMessageRepository;
	ChatMapper chatMapper;

	@Override
	public ChatHistoryResponseDTO getProjectChatHistory(final Long projectId) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Long userId = jwtUserPrincipal.id();

		ChatSession chatSession = chatSessionRepository.getReferenceById(new ChatSessionId(projectId, userId));

		List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

		return new ChatHistoryResponseDTO(chatMapper.toDtos(chatMessageList));
	}
}
