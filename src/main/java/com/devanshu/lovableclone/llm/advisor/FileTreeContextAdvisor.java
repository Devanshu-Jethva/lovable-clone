package com.devanshu.lovableclone.llm.advisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class FileTreeContextAdvisor implements StreamAdvisor {

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

		String tree = "";

		Object object = request.context().get("filePaths");

		if (!(object instanceof List<?> filePaths)) {
			throw new IllegalStateException("filePaths missing from advisor context");
		}

		tree = filePaths.stream().filter(String.class::isInstance).map(String.class::cast).sorted()
				.collect(Collectors.joining("\n"));

		String fileTreeContext = """
				---- FILE_TREE ----
				%s
				""".formatted(tree);

		List<Message> allMessages = new ArrayList<>();

		boolean systemMessageUpdated = false;

		for (Message message : incomingMessages) {
			if (message.getMessageType() == MessageType.SYSTEM && !systemMessageUpdated) {

				SystemMessage originalSystemMessage = (SystemMessage) message;

				allMessages.add(new SystemMessage(originalSystemMessage.getText() + fileTreeContext));

				systemMessageUpdated = true;

			} else {
				allMessages.add(message);
			}
		}

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
