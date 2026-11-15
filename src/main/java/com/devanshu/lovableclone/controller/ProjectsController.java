package com.devanshu.lovableclone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.deploy.DeployResponseDTO;
import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.dto.projects.ProjectRequestDTO;
import com.devanshu.lovableclone.service.DeploymentService;
import com.devanshu.lovableclone.service.ProjectsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectsController {

	private final ProjectsService projectsService;
	private final DeploymentService deploymentService;

	@PostMapping
	public ResponseEntity<Object> createProject(@Valid @RequestBody final ProjectRequestDTO projectRequestDTO) {
		ProjectDetailDTO projectDetailDTO = projectsService.createProject(projectRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProjectDetailDTO>builder()
				.data(projectDetailDTO).message("Project added successfully").build());
	}

	@PutMapping
	@PreAuthorize("@securityExpression.canEditProject(#projectRequestDTO.id())")
	public ResponseEntity<Object> updateProject(@Valid @RequestBody final ProjectRequestDTO projectRequestDTO) {
		ProjectDetailDTO projectDetailDTO = projectsService.updateProject(projectRequestDTO);
		return ResponseEntity.ok(ApiResponse.<ProjectDetailDTO>builder().data(projectDetailDTO)
				.message("Project updated successfully").build());
	}

	@GetMapping
	public ResponseEntity<Object> getAllProjects() {
		List<ProjectListDTO> projectListDTOs = projectsService.getAllProjects();
		return ResponseEntity.ok(ApiResponse.<List<ProjectListDTO>>builder().data(projectListDTOs)
				.message("Projects fetched successfully").build());
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityExpression.canViewProject(#id)")
	public ResponseEntity<Object> getProjectById(@PathVariable final Long id) {
		ProjectDetailDTO projectDetailDTO = projectsService.getProjectById(id);
		return ResponseEntity.ok(ApiResponse.<ProjectDetailDTO>builder().data(projectDetailDTO)
				.message("Project fetched successfully").build());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityExpression.canDeleteProject(#id)")
	public ResponseEntity<Object> deleteProjectById(@PathVariable final Long id) {
		projectsService.deleteProject(id);
		return ResponseEntity.ok(ApiResponse.builder().message("Project deleted successfully").build());
	}

	@PostMapping("/{id}/deploy")
	public ResponseEntity<Object> deployProject(@PathVariable final Long id) {
		return ResponseEntity.ok(ApiResponse.<DeployResponseDTO>builder().data(deploymentService.deploy(id))
				.message("Project deployed successfully").build());
	}
}
