package com.devanshu.lovableclone.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.constant.ApplicationConstants;
import com.devanshu.lovableclone.entity.ProjectFile;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.repository.ProjectFileRepository;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.service.ProjectTemplateService;

import io.minio.CopyObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.SourceObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

	private final MinioClient minioClient;
	private final ProjectFileRepository projectFileRepository;
	private final ProjectsRepository projectsRepository;

	@Override
	public void initializeProjectFromTemplate(final Long projectId) {
		Projects projects = projectsRepository.findById(projectId)
				.orElseThrow(() -> new NotFoundException("Project " + projectId.toString()));

		try {
			Iterable<Result<Item>> results = minioClient
					.listObjects(ListObjectsArgs.builder().bucket(ApplicationConstants.TEMPLATE_BUCKET)
							.prefix(ApplicationConstants.TEMPLATE_NAME + "/").recursive(true).build());

			List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db

			for (Result<Item> result : results) {
				Item item = result.get();
				String sourceKey = item.objectName();

				String cleanPath = sourceKey.replaceFirst(ApplicationConstants.TEMPLATE_NAME + "/", "");
				String destKey = projectId + "/" + cleanPath;

				minioClient.copyObject(CopyObjectArgs
						.builder().bucket(ApplicationConstants.MAIN_BUCKET).object(destKey).source(SourceObject
								.builder().bucket(ApplicationConstants.TEMPLATE_BUCKET).object(sourceKey).build())
						.build());

				ProjectFile pf = ProjectFile.builder().projects(projects).path(cleanPath).minioObjectKey(destKey)
						.build();

				filesToSave.add(pf);
			}

			projectFileRepository.saveAll(filesToSave);

		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize project from template", e);
		}

	}
}