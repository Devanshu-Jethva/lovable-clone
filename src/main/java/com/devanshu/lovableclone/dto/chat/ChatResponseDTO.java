package com.devanshu.lovableclone.dto.chat;

import com.devanshu.lovableclone.constant.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponseDTO(
        Long id,
        MessageRole role,
        List<ChatEventResponseDTO> events,
        String content,
        Integer tokensUsed,
        Instant createdAt
) {
}
