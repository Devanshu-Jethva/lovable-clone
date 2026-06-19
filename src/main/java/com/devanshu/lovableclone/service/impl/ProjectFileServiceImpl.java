package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.projects.file.ProjectFileDetailDTO;
import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.service.ProjectFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFileServiceImpl implements ProjectFileService {
    @Override
    public List<ProjectFileListDTO> getFiles(Long projectId) {
        return List.of();
    }

    @Override
    public ProjectFileDetailDTO getFile(Long projectId, String path) {
        return null;
    }
}
