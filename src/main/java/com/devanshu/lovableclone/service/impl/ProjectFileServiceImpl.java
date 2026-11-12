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
import com.devanshu.lovableclone.exceptions.NotFoundException;
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
		String cleanPath = path.startsWith(ApplicationConstants.FILE_SEPARATOR) ? path.substring(1) : path;
		String objectName = projectId + ApplicationConstants.FILE_SEPARATOR + cleanPath;
		try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
				.bucket(storagePropertiesConfig.getProjectBucket()).object(objectName).build())) {

			String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return new ProjectFileDetailDTO(path, content);
		} catch (Exception e) {
			log.error("Failed to read file: {}/{}", projectId, path, e);
			throw new NotFoundException(path);
		}
	}

	@Override
	public void saveFile(final Long projectId, final String filePath, final String fileContent) {
		log.info("Saving file: {}", filePath);
		String cleanPath = validatePath(filePath);

		Projects projects = projectsService.getProject(projectId);
		String objectKey = projectId + ApplicationConstants.FILE_SEPARATOR + cleanPath;

		try {
			byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
			try (InputStream inputStream = new ByteArrayInputStream(contentBytes)) {

				// Save file content in MinIO
				minioClient.putObject(PutObjectArgs.builder().bucket(storagePropertiesConfig.getProjectBucket())
						.object(objectKey).stream(inputStream, (long) contentBytes.length, -1L)
						.contentType(determineContentType(cleanPath)).build());
			}

			// Save file metadata in PostgreSQL
			ProjectFile file = projectFileRepository.findByProjects_IdAndPath(projectId, cleanPath)
					.orElseGet(() -> ProjectFile.builder().projects(projects).path(cleanPath).build());

			// Maintain the MinIO object key for both insert and update
			file.setMinioObjectKey(objectKey);

			projectFileRepository.save(file);
			log.info("Saved file: {}", objectKey);
		} catch (Exception e) {
			log.error("Failed to save file {}/{}", projectId, cleanPath, e);
			throw new RuntimeException("File save failed", e);
		}
	}

	private String validatePath(final String filePath) {
		if (filePath == null || filePath.isBlank()) {
			throw new RuntimeException("Empty file path");
		}
		String p = filePath.replace('\\', '/');
		while (p.startsWith("/")) {
			p = p.substring(1);
		}
		for (String seg : p.split("/", -1)) {
			if (seg.isEmpty() || seg.equals(".") || seg.equals("..")) {
				throw new RuntimeException("Invalid file path: " + filePath);
			}
		}
		return p;
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
