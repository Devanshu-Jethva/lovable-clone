package com.devanshu.lovableclone.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.dto.chat.ChatRequestDTO;
import com.devanshu.lovableclone.dto.chat.ChatResponseDTO;
import com.devanshu.lovableclone.dto.chat.StreamResponseDTO;
import com.devanshu.lovableclone.service.AiGenerationService;
import com.devanshu.lovableclone.service.ChatService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

	private final AiGenerationService aiGenerationService;
	private final ChatService chatService;

	@PreAuthorize("@securityExpression.canEditProject(#request.projectId())")
	@PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<StreamResponseDTO>> streamChat(@RequestBody final ChatRequestDTO request) {

		return aiGenerationService.streamResponse(request.message(), request.projectId())
				.map(data -> ServerSentEvent.<StreamResponseDTO>builder().data(data).build());
	}

	@GetMapping("/projects/{projectId}")
	public ResponseEntity<List<ChatResponseDTO>> getChatHistory(@PathVariable final Long projectId) {

		return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
	}
}
