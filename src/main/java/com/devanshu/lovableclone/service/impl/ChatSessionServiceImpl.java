package com.devanshu.lovableclone.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.entity.ChatSessionId;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.repository.ChatSessionRepository;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.ChatSessionService;
import com.devanshu.lovableclone.service.HelperService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatSessionServiceImpl implements ChatSessionService {

	HelperService helperService;
	ProjectsRepository projectsRepository;
	ChatSessionRepository chatSessionRepository;
	UsersRepository usersRepository;

	@Override
	public ChatSession createChatSessionIfNotExists(final Long projectId) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Long userId = jwtUserPrincipal.id();

		ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
		Optional<ChatSession> chatSessionOptional = chatSessionRepository.findById(chatSessionId);

		ChatSession chatSession = null;
		if (chatSessionOptional.isEmpty()) {
			Projects projects = projectsRepository.findById(projectId)
					.orElseThrow(() -> new NotFoundException("Project " + projectId));
			Users users = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId));

			chatSession = ChatSession.builder().id(chatSessionId).projects(projects).users(users).build();

			chatSession = chatSessionRepository.save(chatSession);
		} else {
			chatSession = chatSessionOptional.get();
		}
		return chatSession;
	}
}
