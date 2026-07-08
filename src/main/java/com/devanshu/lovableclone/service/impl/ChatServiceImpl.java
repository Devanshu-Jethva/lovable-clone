package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.chat.ChatResponseDTO;
import com.devanshu.lovableclone.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {
    
    @Override
    public List<ChatResponseDTO> getProjectChatHistory(Long projectId) {
        return List.of();
    }
}
