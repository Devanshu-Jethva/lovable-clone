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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional(rollbackFor = Throwable.class)
public class ProjectsServiceImpl implements ProjectsService {

    ProjectsRepository projectsRepository;
    UsersRepository usersRepository;
    ProjectsMapper projectsMapper;

    @Override
    public ProjectDetailDTO createProject(ProjectRequestDTO projectRequestDTO) {
        Users users = usersRepository.findById(1L).get();// TODO

        Projects projects = Projects.builder()
                                    .owner(users)
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
        Users users = new Users(); // TODO: user from security context
        Projects projects = getProject(id);
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public ProjectDetailDTO updateProject(ProjectRequestDTO projectRequestDTO) {
        Users users = new Users(); // TODO: user from security context
        Projects projects = getProject(projectRequestDTO.id());
        if (projects.getDeletedAt() != null) {
            throw new RuntimeException("Project is deleted and cannot be updated");
        }
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        projects.setName(projectRequestDTO.name());
        projects.setDescription(projectRequestDTO.description());
        projects = projectsRepository.save(projects);
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public void deleteProject(Long id) {
        Users users = new Users(); // TODO: user from security context
        Projects projects = getProject(id);
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        if (projects.getDeletedAt() != null) {
            throw new RuntimeException("Project is deleted and cannot be updated");
        }
        projects.setDeletedAt(Instant.now());
        projectsRepository.save(projects);
    }

    private Projects getProject(Long id) {
        return projectsRepository.findById(id)
                                 .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

}
