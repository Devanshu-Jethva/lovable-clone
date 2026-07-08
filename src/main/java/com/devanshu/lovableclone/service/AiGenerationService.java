package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.chat.StreamResponseDTO;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<StreamResponseDTO> streamResponse(String userPrompt, Long projectId);
}
