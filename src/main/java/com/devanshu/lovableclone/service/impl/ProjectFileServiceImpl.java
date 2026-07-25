package com.devanshu.lovableclone.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.config.properties.StoragePropertiesConfig;
import com.devanshu.lovableclone.constant.ApplicationConstants;
import com.devanshu.lovableclone.dto.projects.file.FileTreeResponse;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.entity.ProjectFile;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.mapper.ProjectFileMapper;
import com.devanshu.lovableclone.repository.ProjectFileRepository;
import com.devanshu.lovableclone.service.ProjectFileService;
import com.devanshu.lovableclone.service.ProjectsService;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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
	public FileTreeResponse getFiles(final Long projectId) {
		List<ProjectFile> projectFileList = projectFileRepository.findByProjects_Id(projectId);
		List<ProjectFileListDTO> projectListDTOS = projectFileMapper.toProjectListDTO(projectFileList);
		return new FileTreeResponse(projectListDTOS);
	}

	@Override
	public ProjectFileDetailDTO getFile(final Long projectId, final String path) {
		String objectName = projectId + "/" + path;
		try (InputStream is = minioClient.getObject(
				GetObjectArgs.builder().bucket(ApplicationConstants.MAIN_BUCKET).object(objectName).build())) {

			String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return new ProjectFileDetailDTO(path, content);
		} catch (Exception e) {
			log.error("Failed to read file: {}/{}", projectId, path, e);
			throw new RuntimeException("Failed to read file content", e);
		}
	}

	@Override
	public void saveFile(final Long projectId, final String filePath, final String fileContent) {
		log.info("Saving file: {}", filePath);
		Projects projects = projectsService.getProject(projectId);

		String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
		String objectKey = projectId + "/" + cleanPath;

		try {
			byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
			InputStream inputStream = new ByteArrayInputStream(contentBytes);
			// saving the file content
			minioClient.putObject(PutObjectArgs.builder().bucket(storagePropertiesConfig.getProjectBucket())
					.object(objectKey).stream(inputStream, (long) contentBytes.length, -1L)
					.contentType(determineContentType(filePath)).build());

			// Saving the metaData
			ProjectFile file = projectFileRepository.findByProjects_IdAndPath(projectId, cleanPath).orElseGet(
					() -> ProjectFile.builder().projects(projects).path(cleanPath).minioObjectKey(objectKey).build());

			projectFileRepository.save(file);
			log.info("Saved file: {}", objectKey);
		} catch (Exception e) {
			log.error("Failed to save file {}/{}", projectId, cleanPath, e);
			throw new RuntimeException("File save failed", e);
		}
	}

	private String determineContentType(final String path) {
		String type = URLConnection.guessContentTypeFromName(path);
		if (type != null) {
			return type;
		}
		if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) {
			return "text/javascript";
		}
		if (path.endsWith(".json")) {
			return "application/json";
		}
		if (path.endsWith(".css")) {
			return "text/css";
		}

		return "text/plain";
	}
}
