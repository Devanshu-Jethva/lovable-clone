package com.devanshu.lovableclone.llm.tool;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.devanshu.lovableclone.constant.ReadFileResponse;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.service.ProjectFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools {

	private final ProjectFileService projectFileService;
	private final Long projectId;
	private final Set<String> existingFiles;

	@Tool(name = "read_files", description = """
			Reads the content of EXISTING project files.

			Rules:
			- Request ONLY files present in FILE_TREE.
			- Never request directories.
			- Never request files you intend to create.
			- If a file is not in FILE_TREE, generate it directly.
			""")
	public List<ReadFileResponse> readFiles(
			@ToolParam(description = "List of relative paths (e.g. ['src/App.tsx'])") final List<String> paths) {

		List<ReadFileResponse> result = new ArrayList<>();
		Set<String> uniquePaths = new LinkedHashSet<>(paths);

		for (String requestedPath : uniquePaths) {

			String cleanPath = requestedPath.startsWith("/") ? requestedPath.substring(1) : requestedPath;

			log.info("LLM requested: {}", cleanPath);

			if (cleanPath.endsWith("/")) {
				result.add(new ReadFileResponse(cleanPath, ReadFileResponse.Status.INVALID_REQUEST, null,
						"Directories cannot be read. Only request files listed in FILE_TREE."));
				continue;
			}

			if (!existingFiles.contains(cleanPath)) {
				result.add(new ReadFileResponse(cleanPath, ReadFileResponse.Status.FILE_NOT_FOUND, null,
						"This file is not present in FILE_TREE. Generate it directly and never request it again."));
				continue;
			}

			try {
				String content = projectFileService.getFile(projectId, cleanPath).content();
				result.add(new ReadFileResponse(cleanPath, ReadFileResponse.Status.FILE_FOUND, content, null));
			} catch (NotFoundException e) {
				result.add(new ReadFileResponse(cleanPath, ReadFileResponse.Status.FILE_NOT_FOUND, null,
						"This file could not be read. Generate it directly."));
			}
		}

		return result;
	}
}
