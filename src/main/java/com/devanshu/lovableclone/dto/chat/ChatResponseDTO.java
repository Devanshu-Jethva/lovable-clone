package com.devanshu.lovableclone.dto.chat;

import java.time.Instant;
import java.util.List;

import com.devanshu.lovableclone.constant.MessageRole;

public record ChatResponseDTO(Long id, MessageRole messageRole, List<ChatEventResponseDTO> events, String content,
		Integer tokenUsed, Instant createdAt) {
}
