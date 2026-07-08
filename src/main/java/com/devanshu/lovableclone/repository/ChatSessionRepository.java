package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
