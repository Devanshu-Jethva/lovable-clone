package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.entity.ChatSession;

public interface ChatSessionService {
    ChatSession createChatSessionIfNotExists(Long projectId);
}
