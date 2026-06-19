package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.dto.projects.ProjectRequestDTO;

import java.util.List;

public interface ProjectsService {

    List<ProjectListDTO> getAllProjects();

    ProjectDetailDTO getProjectById(Long id);

    ProjectDetailDTO createProject(ProjectRequestDTO projectRequestDTO);

    ProjectDetailDTO updateProject(ProjectRequestDTO projectRequestDTO);

    void deleteProject(Long id);
}
