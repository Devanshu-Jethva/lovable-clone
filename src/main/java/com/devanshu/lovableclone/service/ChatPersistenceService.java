package com.devanshu.lovableclone.service;

import java.util.List;

import org.springframework.ai.chat.metadata.Usage;

import com.devanshu.lovableclone.entity.ChatEvent;
import com.devanshu.lovableclone.entity.ChatSession;

public interface ChatPersistenceService {

	List<ChatEvent> saveChat(ChatSession chatSession, String userMessage, String fullText, Long duration, Usage usage);

}
