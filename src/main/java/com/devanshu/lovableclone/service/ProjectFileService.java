package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.file.FileTreeResponse;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;

public interface ProjectFileService {

    FileTreeResponse getFiles(Long projectId);

    ProjectFileDetailDTO getFile(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
