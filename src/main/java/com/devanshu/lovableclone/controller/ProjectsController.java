package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.GenericResponseHandler;
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
        return GenericResponseHandler.builder().data(projectDetailDTO).status(HttpStatus.CREATED)
                                     .message("Project added successfully").build();
    }

    @PutMapping
    public ResponseEntity<Object> updateProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectDetailDTO projectDetailDTO = projectsService.updateProject(projectRequestDTO);
        return GenericResponseHandler.builder().data(projectDetailDTO).status(HttpStatus.OK)
                                     .message("Project updated successfully").build();
    }

    @GetMapping
    public ResponseEntity<Object> getAllProjects() {
        Long userId = 1L; // TODO
        List<ProjectListDTO> projectListDTOs = projectsService.getAllProjects();
        return GenericResponseHandler.builder().data(projectListDTOs)
                                     .status(HttpStatus.OK)
                                     .message("Projects fetched successfully").build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProjectById(@PathVariable Long id) {
        Long userId = 1L;
        ProjectDetailDTO projectDetailDTO = projectsService.getProjectById(id);
        return GenericResponseHandler.builder().data(projectDetailDTO).status(HttpStatus.OK)
                                     .message("Project fetched successfully").build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProjectById(@PathVariable Long id) {
        projectsService.deleteProject(id);
        return GenericResponseHandler.builder().status(HttpStatus.OK).message("Project deleted successfully").build();
    }
}
