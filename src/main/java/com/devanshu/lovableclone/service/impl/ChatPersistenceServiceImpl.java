package com.devanshu.lovableclone.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.constant.ChatEventType;
import com.devanshu.lovableclone.constant.MessageRole;
import com.devanshu.lovableclone.entity.ChatEvent;
import com.devanshu.lovableclone.entity.ChatMessage;
import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.llm.LlmResponseParser;
import com.devanshu.lovableclone.repository.ChatEventRepository;
import com.devanshu.lovableclone.repository.ChatMessageRepository;
import com.devanshu.lovableclone.service.ChatPersistenceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatPersistenceServiceImpl implements ChatPersistenceService {

	private final LlmResponseParser llmResponseParser;
	private final ChatMessageRepository chatMessageRepository;
	private final ChatEventRepository chatEventRepository;

	@Override
	@Transactional
	public List<ChatEvent> saveChat(final ChatSession chatSession, final String userMessage, final String fullText,
			final Long duration, final Usage usage) {
		// Save the User message
		chatMessageRepository.save(ChatMessage.builder().chatSession(chatSession).messageRole(MessageRole.USER)
				.content(userMessage).tokenUsed(usage != null ? usage.getPromptTokens() : null).build());

		ChatMessage assistantChatMessage = ChatMessage.builder().messageRole(MessageRole.ASSISTANT)
				.content(StringUtils.isNotBlank(fullText) ? fullText : "Assistant Message here...")
				.chatSession(chatSession).tokenUsed(usage != null ? usage.getCompletionTokens() : null).build();

		assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

		List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
		chatEventList.addFirst(ChatEvent.builder().type(ChatEventType.THOUGHT).chatMessage(assistantChatMessage)
				.content("Thought for " + duration + "s").sequenceOrder(0).build());

		chatEventRepository.saveAll(chatEventList);
		return chatEventList;
	}

}
