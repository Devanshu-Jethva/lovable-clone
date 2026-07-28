package com.devanshu.lovableclone.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.devanshu.lovableclone.dto.chat.ChatResponseDTO;
import com.devanshu.lovableclone.entity.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMapper {

	List<ChatResponseDTO> toDtos(final List<ChatMessage> chatMessages);

}
