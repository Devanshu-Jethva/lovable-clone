package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.dto.projects.ProjectRequestDTO;
import com.devanshu.lovableclone.service.ProjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectsController {

    private final ProjectsService projectsService;

    @PostMapping
    public ResponseEntity<Object> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectDetailDTO projectDetailDTO = projectsService.createProject(projectRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(ApiResponse.<ProjectDetailDTO>builder()
                                            .data(projectDetailDTO)
                                            .message("Project added successfully")
                                            .build());
    }

    @PutMapping
    public ResponseEntity<Object> updateProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectDetailDTO projectDetailDTO = projectsService.updateProject(projectRequestDTO);
        return ResponseEntity.ok(ApiResponse.<ProjectDetailDTO>builder()
                                            .data(projectDetailDTO)
                                            .message("Project updated successfully")
                                            .build());
    }

    @GetMapping
    public ResponseEntity<Object> getAllProjects() {
        Long userId = 1L; // TODO
        List<ProjectListDTO> projectListDTOs = projectsService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.<List<ProjectListDTO>>builder()
                                            .data(projectListDTOs)
                                            .message("Projects fetched successfully")
                                            .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProjectById(@PathVariable Long id) {
        Long userId = 1L;
        ProjectDetailDTO projectDetailDTO = projectsService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.<ProjectDetailDTO>builder()
                                            .data(projectDetailDTO)
                                            .message("Project fetched successfully")
                                            .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProjectById(@PathVariable Long id) {
        projectsService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.builder()
                                            .message("Project deleted successfully")
                                            .build());
    }
}
