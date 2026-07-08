package com.devanshu.lovableclone.service.impl;

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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatSessionServiceImpl implements ChatSessionService {

    HelperService helperService;
    ProjectsRepository projectsRepository;
    ChatSessionRepository chatSessionRepository;
    UsersRepository usersRepository;

    @Override
    public ChatSession createChatSessionIfNotExists(Long projectId) {
        JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
        Long userId = jwtUserPrincipal.id();

        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            Projects projects = projectsRepository.findById(projectId)
                                                  .orElseThrow(() -> new NotFoundException("Project " + projectId));
            Users users = usersRepository.findById(userId)
                                         .orElseThrow(() -> new NotFoundException("User " + userId));

            chatSession = ChatSession.builder()
                                     .id(chatSessionId)
                                     .projects(projects)
                                     .users(users)
                                     .build();

            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}
