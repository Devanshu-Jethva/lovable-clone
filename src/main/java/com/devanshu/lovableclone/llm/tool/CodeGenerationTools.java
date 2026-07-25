package com.devanshu.lovableclone.llm.tool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.devanshu.lovableclone.service.ProjectFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools {

	private final ProjectFileService projectFileService;
	private final Long projectId;

	@Tool(name = "read_files", description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
	public List<String> readFiles(
			@ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])") final List<String> paths) {

		List<String> result = new ArrayList<>();

		for (String path : paths) {
			String cleanPath = path.startsWith("/") ? path.substring(1) : path;

			log.info("Requested file: {}", cleanPath);

			String content = projectFileService.getFile(projectId, cleanPath).content();

			result.add("""
					--- START OF FILE: %s ---
					%s
					--- END OF FILE ---
					""".formatted(cleanPath, content));
		}

		return result;
	}
}
