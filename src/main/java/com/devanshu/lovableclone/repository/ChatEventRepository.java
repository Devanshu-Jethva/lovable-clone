package com.devanshu.lovableclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.entity.ChatEvent;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {

}
