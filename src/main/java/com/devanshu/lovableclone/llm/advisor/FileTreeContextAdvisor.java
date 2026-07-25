package com.devanshu.lovableclone.llm.advisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.service.ProjectFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

	private final ProjectFileService projectFileService;

	@Override
	public Flux<ChatClientResponse> adviseStream(final ChatClientRequest request,
			final StreamAdvisorChain streamAdvisorChain) {
		Map<String, Object> context = request.context();
		Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());

		ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);

		return streamAdvisorChain.nextStream(augmentedChatClientRequest);
	}

	private ChatClientRequest augmentRequestWithFileTree(final ChatClientRequest request, final Long projectId) {

		List<Message> incomingMessages = request.prompt().getInstructions();

		Message systemMessage = incomingMessages.stream().filter(m -> m.getMessageType() == MessageType.SYSTEM)
				.findFirst().orElse(null);

		List<Message> userMessages = incomingMessages.stream().filter(m -> m.getMessageType() != MessageType.SYSTEM)
				.toList();

		List<Message> allMessages = new ArrayList<>();

		// Add original system message
		if (systemMessage != null) {
			allMessages.add(systemMessage);
		}

		List<ProjectFileListDTO> fileTree = projectFileService.getFiles(projectId).files();
		String fileTreeContext = "\n\n ---- FILE_TREE ----\n" + fileTree.toString();
		allMessages.add(new SystemMessage(fileTreeContext));

		allMessages.addAll(userMessages);

		return request.mutate().prompt(new Prompt(allMessages, request.prompt().getOptions())).build();
	}

	@Override
	public String getName() {
		return "FileTreeContextAdvisor";
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
