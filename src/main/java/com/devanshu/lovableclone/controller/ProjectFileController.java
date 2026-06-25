package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/file")
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<Object> getFiles(@PathVariable Long projectId) {
        List<ProjectFileListDTO> files = projectFileService.getFiles(projectId);
        return ResponseEntity.ok(ApiResponse.<List<ProjectFileListDTO>>builder()
                                            .message("File fetched successfully")
                                            .data(files)
                                            .build());
    }

    /*
     * e.g. path = /src/hook/feature.tsx
     */
    @GetMapping("/{*path}")
    public ResponseEntity<Object> getFile(@PathVariable Long projectId, @PathVariable String path) {
        ProjectFileDetailDTO projectFileDetailDTO = projectFileService.getFile(projectId, path);
        return ResponseEntity.ok(ApiResponse.<ProjectFileDetailDTO>builder()
                                            .message("File fetched successfully")
                                            .data(projectFileDetailDTO)
                                            .build());
    }
}
