package com.devanshu.lovableclone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devanshu.lovableclone.entity.ChatMessage;
import com.devanshu.lovableclone.entity.ChatSession;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	@Query("""
			SELECT DISTINCT m FROM ChatMessage m
			LEFT JOIN FETCH m.events e
			WHERE m.chatSession = :chatSession
			ORDER BY m.createdAt ASC, e.sequenceOrder ASC
			""")
	List<ChatMessage> findByChatSession(ChatSession chatSession);
}
