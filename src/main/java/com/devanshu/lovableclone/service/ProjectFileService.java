package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;

import java.util.List;

public interface ProjectFileService {

    List<ProjectFileListDTO> getFiles(Long projectId);

    ProjectFileDetailDTO getFile(Long projectId, String path);
}
