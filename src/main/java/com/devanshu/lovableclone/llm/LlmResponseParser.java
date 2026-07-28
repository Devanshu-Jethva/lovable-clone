package com.devanshu.lovableclone.llm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.devanshu.lovableclone.constant.ChatEventType;
import com.devanshu.lovableclone.entity.ChatEvent;
import com.devanshu.lovableclone.entity.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LlmResponseParser {

	/**
	 * Regex Breakdown: <br/>
	 * Group 1: Opening Tag (<tag ...>) <br/>
	 * Group 2: Tag Name (message|file|read-file-tool) <br/>
	 * Group 3: Attributes part (e.g., ' path="foo"' or ' args="a,b"') <br/>
	 * Group 4: Content (The stuff inside) <br/>
	 * Group 5: Closing Tag (</tag>) <br/>
	 */
	private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile(
			"(<(message|file|read-file-tool)([^>]*)>)([\\s\\S]*?)(</\\2>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	// Helper to extract specific attributes (path="..." or args="...") from Group 3
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(path|args)=\"([^\"]+)\"");

	public List<ChatEvent> parseChatEvents(final String fullResponse, final ChatMessage parentMessage) {
		List<ChatEvent> events = new ArrayList<>();
		int orderCounter = 1;

		Matcher matcher = GENERIC_TAG_PATTERN.matcher(fullResponse);

		while (matcher.find()) {
			String tagName = matcher.group(2).toLowerCase();
			String attributes = matcher.group(3);
			String content = matcher.group(4).trim();

			// Extract attributes map
			Map<String, String> attrMap = extractAttributes(attributes);

			// content here is your Markdown content
			ChatEvent.ChatEventBuilder builder = ChatEvent.builder().chatMessage(parentMessage).content(content)
					.sequenceOrder(orderCounter++);

			switch (tagName) {
			case "message" -> builder.type(ChatEventType.MESSAGE);
			case "file" -> {
				builder.type(ChatEventType.FILE_EDIT);
				builder.filePath(attrMap.get("path")); // Required for files
//                    builder.content(null);
			}
			case "read-file-tool" -> {
				builder.type(ChatEventType.TOOL_LOG);
				builder.metadata(attrMap.get("args")); // Store raw file list in metadata
			}
			default -> {
				continue;
			}
			}

			events.add(builder.build());
		}

		return events;
	}

	private Map<String, String> extractAttributes(final String attributeString) {
		Map<String, String> attributes = new HashMap<>();
		if (attributeString == null) {
			return attributes;
		}

		Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributeString);
		while (matcher.find()) {
			attributes.put(matcher.group(1), matcher.group(2));
		}
		return attributes;
	}

}
