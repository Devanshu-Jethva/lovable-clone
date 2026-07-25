package com.devanshu.lovableclone.service.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.dto.chat.StreamResponseDTO;
import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.llm.advisor.FileTreeContextAdvisor;
import com.devanshu.lovableclone.llm.tool.CodeGenerationTools;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.AiGenerationService;
import com.devanshu.lovableclone.service.ChatSessionService;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.ProjectFileService;
import com.devanshu.lovableclone.util.PromptUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiGenerationServiceImpl implements AiGenerationService {

	private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>",
			Pattern.DOTALL);
	ChatClient chatClient;
	HelperService helperService;
	ChatSessionService chatSessionService;
	ProjectFileService projectFileService;
	FileTreeContextAdvisor fileTreeContextAdvisor;

	@Override
	public Flux<StreamResponseDTO> streamResponse(final String userPrompt, final Long projectId) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		ChatSession chatSession = chatSessionService.createChatSessionIfNotExists(projectId);

		/*
		 * this is the context for our advisors and we can get it in advisors
		 */
		Map<String, Object> advisorParams = Map.of("userId", jwtUserPrincipal.id(), "projectId", projectId);

		StringBuilder fullResponseBuffer = new StringBuilder();
		CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

		AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
		AtomicReference<Long> endTime = new AtomicReference<>(0L);
		AtomicReference<Usage> usageRef = new AtomicReference<>();

		return chatClient.prompt().system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT).user(userPrompt)
				.tools(codeGenerationTools).advisors(advisorSpec -> {
					advisorSpec.params(advisorParams);
					advisorSpec.advisors(fileTreeContextAdvisor);
				}).stream().chatResponse().doOnNext(response -> {
					String content = response.getResult().getOutput().getText();

					if (content != null && !content.isEmpty() && endTime.get() == 0) { // first non-empty chunk received
						endTime.set(System.currentTimeMillis());
					}

					usageRef.set(response.getMetadata().getUsage());

					fullResponseBuffer.append(content);
				}).doOnComplete(() -> {
					Schedulers.boundedElastic().schedule(() -> {
						parseAndSaveFiles(fullResponseBuffer.toString(), projectId);

//                                 long duration = (endTime.get() - startTime.get()) / 1000;
//                                 finalizeChats(userPrompt, chatSession, fullResponseBuffer.toString(), duration,
//                                         usageRef.get());
					});
				}).doOnError(error -> log.error("Error during streaming for projectId: {}, {}", projectId,
						error.getMessage()))
				.map(response -> {
					String text = response.getResult().getOutput().getText();
					return new StreamResponseDTO(text != null ? text : "");
				});
	}

	private void parseAndSaveFiles(final String fullResponse, final Long projectId) {
//        String dummy = """
//                <message> I'm going to read the files and generate the code </message>
//                <file path="src/App.jsx">
//                    import App from './App.jsx'
//                    .....
//                </file>
//                <message> I'm going to read the files and generate the code </message>
//                <file path="src/App.jsx">
//                    import App from './App.jsx'
//                    .....
//                </file>
//                """;

		Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
		while (matcher.find()) {
			String filePath = matcher.group(1);
			String fileContent = matcher.group(2).trim();

			projectFileService.saveFile(projectId, filePath, fileContent);
		}
	}

//    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage) {
//        Long projectId = chatSession.getProjects().getId();
//
//        if (usage != null) {
//            int totalTokens = usage.getTotalTokens();
//            usageService.recordTokenUsage(chatSession.getUsers().getId(), totalTokens);
//        }
//
//        // Save the User message
//        chatMessageRepository.save(
//                ChatMessage.builder()
//                           .chatSession(chatSession)
//                           .messageRole(MessageRole.USER)
//                           .content(userMessage)
//                           .tokenUsed(usage.getPromptTokens())
//                           .build()
//        );
//
//        ChatMessage assistantChatMessage = ChatMessage.builder()
//                                                      .messageRole(MessageRole.ASSISTANT)
//                                                      .content("Assistant Message here...")
//                                                      .chatSession(chatSession)
//                                                      .tokenUsed(usage.getCompletionTokens())
//                                                      .build();
//
//        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);
//
//        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
//        chatEventList.addFirst(ChatEvent.builder()
//                                        .type(ChatEventType.THOUGHT)
//                                        .chatMessage(assistantChatMessage)
//                                        .content("Thought for " + duration + "s")
//                                        .sequenceOrder(0)
//                                        .build());
//
//        chatEventList.stream()
//                     .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
//                     .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));
//
//        chatEventRepository.saveAll(chatEventList);
//    }
}
