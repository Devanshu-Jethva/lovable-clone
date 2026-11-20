package com.devanshu.lovableclone.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.constant.ChatEventType;
import com.devanshu.lovableclone.constant.GenerationStatus;
import com.devanshu.lovableclone.dto.chat.StreamResponseDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.entity.ChatEvent;
import com.devanshu.lovableclone.entity.ChatSession;
import com.devanshu.lovableclone.llm.AiErrorHandlerUtil;
import com.devanshu.lovableclone.llm.advisor.FileTreeContextAdvisor;
import com.devanshu.lovableclone.llm.tool.CodeGenerationTools;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.AiGenerationService;
import com.devanshu.lovableclone.service.ChatPersistenceService;
import com.devanshu.lovableclone.service.ChatSessionService;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.ProjectFileService;
import com.devanshu.lovableclone.service.UsageService;
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

	ChatClient chatClient;
	HelperService helperService;
	ChatSessionService chatSessionService;
	ProjectFileService projectFileService;
	FileTreeContextAdvisor fileTreeContextAdvisor;
	UsageService usageService;
	ChatPersistenceService chatPersistenceService;

	static Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

	@Override
	public Flux<StreamResponseDTO> streamResponse(final String userPrompt, final Long projectId) {
		usageService.checkDailyTokensUsage();
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		ChatSession chatSession = chatSessionService.createChatSessionIfNotExists(projectId);

		List<String> filePaths = projectFileService.getFiles(projectId).files().stream().map(ProjectFileListDTO::path)
				.toList();

		/*
		 * this is the context for our advisors and we can get it in advisors
		 */
		Map<String, Object> advisorParams = Map.of("userId", jwtUserPrincipal.id(), "projectId", projectId, "filePaths",
				filePaths);

		StringBuffer fullResponseBuffer = new StringBuffer();

		CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId,
				Set.copyOf(filePaths));

		AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
		AtomicReference<Long> endTime = new AtomicReference<>(0L);
		AtomicReference<Usage> usageRef = new AtomicReference<>();

		AtomicBoolean persisted = new AtomicBoolean(false);
		AtomicReference<GenerationStatus> status = new AtomicReference<>(GenerationStatus.COMPLETED);
		Executor io = command -> Schedulers.boundedElastic().schedule(command);
		/*
		 * Persist chat exactly once, irrespective of how the stream terminates.
		 */
		Runnable persist = () -> {
			if (!persisted.compareAndSet(false, true)) {
				return;
			}
			try {
				long end = endTime.get() == 0 ? System.currentTimeMillis() : endTime.get();
				finalizeChats(userPrompt, chatSession, fullResponseBuffer.toString(), (end - startTime.get()) / 1000,
						usageRef.get());
				log.info("Chat finalized for projectId: {} with status: {}", projectId, status.get());
			} catch (Exception e) {
				log.error("Error while finalizing chat for projectId: {}", projectId, e);
			}
		};

		return chatClient.prompt().system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT).user(userPrompt)
				.tools(codeGenerationTools).advisors(advisorSpec -> {
					advisorSpec.params(advisorParams);
					advisorSpec.advisors(fileTreeContextAdvisor);
				}).stream().chatResponse()
				/*
				 * Accumulate response and capture token usage.
				 */
				.doOnNext(response -> {
					if (response.getResult() != null && response.getResult().getOutput() != null) {

						String content = response.getResult().getOutput().getText();

						if (StringUtils.isNotEmpty(content)) {
							fullResponseBuffer.append(content);
						}
					}

					if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
						usageRef.set(response.getMetadata().getUsage());
					}
				})
				// Convert LLM response into StreamResponseDTO
				.filter(r -> r.getResult() != null && r.getResult().getOutput() != null
						&& StringUtils.isNotEmpty(r.getResult().getOutput().getText()))
				.map(r -> new StreamResponseDTO(r.getResult().getOutput().getText(), false, null))
				/*
				 * Log streaming errors.
				 */
				.doOnError(error -> log.error("Error during streaming for projectId: {}", projectId, error))
				/*
				 * Handle completion, cancellation and failure.
				 */
				.doFinally(signalType -> {

					endTime.set(System.currentTimeMillis());

					switch (signalType) {

					case ON_COMPLETE -> status.set(GenerationStatus.COMPLETED);

					case CANCEL -> status.set(GenerationStatus.CANCELLED);

					case ON_ERROR -> status.set(GenerationStatus.FAILED);

					default -> log.warn("Unexpected stream termination: {}", signalType);
					}

					log.info("AI stream terminated for projectId: {} with signal: {}", projectId, signalType);

					io.execute(persist);
				})
				// Persist chat after successful completion
//				.doOnComplete(() -> {
//					try {
//						Schedulers.boundedElastic().schedule(() -> {
//							try {
////								parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
//
//								long end = endTime.get() == 0 ? System.currentTimeMillis() : endTime.get();
//								long duration = (end - startTime.get()) / 1000;
//								finalizeChats(userPrompt, chatSession, fullResponseBuffer.toString(), duration,
//										usageRef.get());
//							} catch (Exception e) {
//								log.error("Error while finalizing chat for projectId: {}", projectId, e);
//							}
//						});
//					} catch (Exception e) {
//						log.error("Unable to schedule chat finalization for projectId: {}", projectId, e);
//					}
//				})
				// Send structured error response to frontend
				.onErrorResume(error -> {
					log.error("AI generation failed for projectId: {}", projectId, error);
					String errorMessage = AiErrorHandlerUtil.getUserFriendlyMessage(error);

					return Flux.just(new StreamResponseDTO(null, true, errorMessage));
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

	private void finalizeChats(final String userMessage, final ChatSession chatSession, final String fullText,
			final Long duration, final Usage usage) {
		if (usage != null) {
			try {
				usageService.recordTokenUsage(chatSession.getUsers().getId(), usage.getTotalTokens());
			} catch (Exception e) {
				log.error("Usage record failed", e);
			}
		}
		List<ChatEvent> events = chatPersistenceService.saveChat(chatSession, userMessage, fullText, duration, usage);
		Long projectId = chatSession.getProjects().getId();
		events.stream().filter(e -> e.getType() == ChatEventType.FILE_EDIT)
				.collect(Collectors.toMap(ChatEvent::getFilePath, e -> e, (a, b) -> b, LinkedHashMap::new)).values()
				.forEach(e -> {
					try {
						projectFileService.saveFile(projectId, e.getFilePath(), e.getContent());
					} catch (Exception ex) {
						log.error("File save failed: {}", e.getFilePath(), ex);
					}
				});
	}
}
