package com.devanshu.lovableclone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.projects.file.FileTreeResponse;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.service.ProjectFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/file")
public class ProjectFileController {

	private final ProjectFileService projectFileService;

	@GetMapping
	public ResponseEntity<Object> getFiles(@PathVariable final Long projectId) {
		FileTreeResponse fileTreeResponse = projectFileService.getFiles(projectId);
		return ResponseEntity.ok(ApiResponse.<FileTreeResponse>builder().message("File fetched successfully")
				.data(fileTreeResponse).build());
	}

	/*
	 * e.g. path = /src/hook/feature.tsx
	 */
	@GetMapping("/{*path}")
	public ResponseEntity<Object> getFile(@PathVariable final Long projectId, @PathVariable final String path) {
		ProjectFileDetailDTO projectFileDetailDTO = projectFileService.getFile(projectId, path);
		return ResponseEntity.ok(ApiResponse.<ProjectFileDetailDTO>builder().message("File fetched successfully")
				.data(projectFileDetailDTO).build());
	}
}
