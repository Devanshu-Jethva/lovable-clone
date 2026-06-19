package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.dto.projects.ProjectRequestDTO;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.mapper.ProjectsMapper;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.service.ProjectsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional(rollbackOn = Throwable.class)
public class ProjectsServiceImpl implements ProjectsService {

    ProjectsRepository projectsRepository;
    UsersRepository usersRepository;
    ProjectsMapper projectsMapper;

    @Override
    public ProjectDetailDTO createProject(ProjectRequestDTO projectRequestDTO) {
        Users users = usersRepository.findById(1L).get();// TODO

        Projects projects = Projects.builder()
                                    .owners(users)
                                    .name(projectRequestDTO.name())
                                    .description(projectRequestDTO.description())
                                    .build();
        projects = projectsRepository.save(projects);
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public List<ProjectListDTO> getAllProjects() {
        List<Projects> projects = projectsRepository.getAllAccessibleProjects(1L);
        return projectsMapper.toProjectListDTOs(projects);
    }

    @Override
    public ProjectDetailDTO getProjectById(Long id) {
        Projects projects = projectsRepository.findById(id).orElseThrow();
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public ProjectDetailDTO updateProject(ProjectRequestDTO projectRequestDTO) {
        return null;
    }

    @Override
    public void deleteProject(Long id) {
        
    }
}
