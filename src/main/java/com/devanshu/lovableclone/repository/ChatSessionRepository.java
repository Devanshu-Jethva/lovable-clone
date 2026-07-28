package com.devanshu.lovableclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.entity.ChatSessionId;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
