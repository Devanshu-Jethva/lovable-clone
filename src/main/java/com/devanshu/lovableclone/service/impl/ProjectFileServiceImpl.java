package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.config.properties.StoragePropertiesConfig;
import com.devanshu.lovableclone.dto.projects.file.FileTreeResponse;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.entity.ProjectFile;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.mapper.ProjectFileMapper;
import com.devanshu.lovableclone.repository.ProjectFileRepository;
import com.devanshu.lovableclone.service.ProjectFileService;
import com.devanshu.lovableclone.service.ProjectsService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectFileServiceImpl implements ProjectFileService {

    ProjectFileRepository projectFileRepository;
    ProjectsService projectsService;
    MinioClient minioClient;
    StoragePropertiesConfig storagePropertiesConfig;
    ProjectFileMapper projectFileMapper;

    @Override
    public FileTreeResponse getFiles(Long projectId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjects_Id(projectId);
        List<ProjectFileListDTO> projectListDTOS = projectFileMapper.toProjectListDTO(projectFileList);
        return new FileTreeResponse(projectListDTOS);
    }

    @Override
    public ProjectFileDetailDTO getFile(Long projectId, String path) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        Projects projects = projectsService.getProject(projectId);

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            // saving the file content
            minioClient.putObject(PutObjectArgs.builder()
                                               .bucket(storagePropertiesConfig.getProjectBucket())
                                               .object(objectKey)
                                               .stream(inputStream, (long) contentBytes.length, -1L)
                                               .contentType(determineContentType(filePath))
                                               .build());

            // Saving the metaData
            ProjectFile file = projectFileRepository.findByProjects_IdAndPath(projectId, cleanPath)
                                                    .orElseGet(() -> ProjectFile.builder()
                                                                                .projects(projects)
                                                                                .path(cleanPath)
                                                                                .minioObjectKey(objectKey) // Use the key we generated
                                                                                .build());

            projectFileRepository.save(file);
            log.info("Saved file: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }
    }

    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }
}
