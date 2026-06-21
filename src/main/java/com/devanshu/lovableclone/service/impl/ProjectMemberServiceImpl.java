package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.constant.ProjectMemberStatus;
import com.devanshu.lovableclone.constant.ProjectRole;
import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.ProjectMemberId;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.mapper.ProjectMemberMapper;
import com.devanshu.lovableclone.repository.ProjectMemberRepository;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectsRepository projectsRepository;
    ProjectMemberMapper projectMemberMapper;
    HelperService helperService;

    @Override
    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        List<ProjectMemberDTO> projectMemberDTOS = new ArrayList<>();
        Projects projects = getProject(projectId);
        projectMemberDTOS.add(projectMemberMapper.toDto(projects.getOwner()));
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjects(projects);
        projectMemberDTOS.addAll(projectMemberMapper.toDtos(projectMembers));
        return projectMemberDTOS;
    }

    @Override
    public ProjectMemberDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        Users users = new Users(); // TODO: user from security context
        if (projectMemberRepository.existsByProjects_IdAndUsers_Id(projectId, users.getId())) {
            throw new RuntimeException("User is already a member of the project");
        }

        if (inviteProjectMemberRequestDTO.email().equals(users.getEmail())) {
            throw new RuntimeException("User cannot invite themselves");
        }
        Projects projects = getProject(projectId);
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        Users invitedUser = helperService.getUserByEmail(inviteProjectMemberRequestDTO.email());
//        if (projectMemberRepository.existsById(new ProjectMemberId(invitedUser.getId(), projectId))) {
//            throw new RuntimeException("User is already a member of the project");
//        }
        if (invitedUser.getDeletedAt() != null) {
            throw new RuntimeException("User with email " + inviteProjectMemberRequestDTO.email() + " is deleted");
        }
        ProjectMember projectMember = ProjectMember.builder().users(invitedUser).projects(projects)
                                                   .id(new ProjectMemberId(invitedUser.getId(), projectId))
                                                   .projectRole(inviteProjectMemberRequestDTO.projectRole())
                                                   .invitedAt(Instant.now())
                                                   .build();
        projectMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toDto(projectMember);
    }

    @Override
    public ProjectMemberDTO updateProjectMemberRole(Long projectId, Long projectMemberId, String projectRole) {
        Users users = new Users(); // TODO: user from security context
        Projects projects = getProject(projectId);
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        ProjectMember projectMember = projectMemberRepository.findById(new ProjectMemberId(projectMemberId, projectId))
                                                             .orElseThrow(() -> new RuntimeException("Project member not found with id: " + projectMemberId));
        projectMember.setProjectRole(ProjectRole.valueOf(projectRole));
        projectMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toDto(projectMember);
    }

    @Override
    public void deleteProjectMember(Long projectId, Long projectMemberId) {
        Users users = new Users(); // TODO: user from security context
        Projects projects = getProject(projectId);
        if (!Objects.equals(projects.getOwner().getId(), users.getId())) {
            throw new RuntimeException("User is not the owner of the project");
        }
        ProjectMember projectMember = projectMemberRepository.findById(new ProjectMemberId(projectMemberId, projectId))
                                                             .orElseThrow(() -> new RuntimeException("Project member not found with id: " + projectMemberId));

        projectMemberRepository.delete(projectMember);
    }

    @Override
    public void acceptRejectInviteProjectMember(Long projectId, String status) {
        Users users = new Users(); // TODO: user from security context
        getProject(projectId); // just to check if project exists with given projectId or not
        ProjectMember projectMember = projectMemberRepository.findById(new ProjectMemberId(users.getId(), projectId))
                                                             .orElseThrow(() -> new RuntimeException("User is not invited"));
        if (StringUtils.hasText(projectMember.getStatus().name())) {
            throw new RuntimeException("User has already responded to the invitation");
        }
        projectMember.setStatus(ProjectMemberStatus.valueOf(status));
        projectMemberRepository.save(projectMember);
    }

    private Projects getProject(Long id) {
        return projectsRepository.findById(id)
                                 .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }
}
