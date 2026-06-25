package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.constant.ProjectMemberStatus;
import com.devanshu.lovableclone.constant.ProjectRole;
import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.dto.projects.ProjectRequestDTO;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.ProjectMemberId;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.BadRequestException;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.exceptions.PermissionException;
import com.devanshu.lovableclone.mapper.ProjectsMapper;
import com.devanshu.lovableclone.repository.ProjectMemberRepository;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.service.ProjectMemberService;
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
    ProjectMemberRepository projectMemberRepository;
    ProjectMemberService projectMemberService;

    @Override
    public ProjectDetailDTO createProject(ProjectRequestDTO projectRequestDTO) {
        Users users = usersRepository.findById(1L).get();// TODO

        Projects projects = Projects.builder()
                                    .name(projectRequestDTO.name())
                                    .description(projectRequestDTO.description())
                                    .build();
        projects = projectsRepository.save(projects);

        ProjectMember projectMember = ProjectMember.builder()
                                                   .id(new ProjectMemberId(users.getId(), projects.getId()))
                                                   .projects(projects)
                                                   .users(users)
                                                   .projectRole(ProjectRole.OWNER)
                                                   .invitedAt(Instant.now())
                                                   .status(ProjectMemberStatus.ACCEPTED)
                                                   .build();
        projectMemberRepository.save(projectMember);
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public List<ProjectListDTO> getAllProjects() {
        Users users = usersRepository.findById(1L).get();// TODO
        List<Projects> projects = projectsRepository.getAllAccessibleProjects(users.getId());
        return projectsMapper.toProjectListDTOs(projects);
    }

    @Override
    public ProjectDetailDTO getProjectById(Long id) {
        Users users = usersRepository.findById(1L).get();// TODO: user from security context
        Projects projects = getProject(id);
        ProjectMember projectOwner = projectMemberService.getProjectOwner(projects);
        if (!Objects.equals(projectOwner.getUsers().getId(), users.getId())) {
            throw new PermissionException("User is not the owner of the project");
        }
        return projectsMapper.toDetailDto(projects);
    }

    @Override
    public ProjectDetailDTO updateProject(ProjectRequestDTO projectRequestDTO) {
        if (projectRequestDTO.id() == null) {
            throw new BadRequestException("Project id is required for update");
        }
        Users users = usersRepository.findById(1L).get();// TODO: user from security context
        Projects projects = getProject(projectRequestDTO.id());
        if (projects.getDeletedAt() != null) {
            throw new BadRequestException("Project is deleted and cannot be updated");
        }
        ProjectMember projectOwner = projectMemberService.getProjectOwner(projects);
        if (!Objects.equals(projectOwner.getUsers().getId(), users.getId())) {
            throw new PermissionException("User is not the owner of the project");
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
        ProjectMember projectOwner = projectMemberService.getProjectOwner(projects);
        if (!Objects.equals(projectOwner.getUsers().getId(), users.getId())) {
            throw new PermissionException("User is not the owner of the project");
        }
        if (projects.getDeletedAt() != null) {
            throw new BadRequestException("Project is already deleted");
        }
        projects.setDeletedAt(Instant.now());
        projectsRepository.save(projects);
    }

    private Projects getProject(Long id) {
        return projectsRepository.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Project with " + id + " not found"));
    }

}
