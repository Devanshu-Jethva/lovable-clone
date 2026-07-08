package com.devanshu.lovableclone.dto.chat;

import com.devanshu.lovableclone.constant.ChatEventType;

public record ChatEventResponseDTO(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
