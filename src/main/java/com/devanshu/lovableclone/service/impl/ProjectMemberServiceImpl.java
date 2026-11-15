package com.devanshu.lovableclone.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devanshu.lovableclone.constant.ProjectMemberStatus;
import com.devanshu.lovableclone.constant.ProjectRole;
import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.ProjectMemberId;
import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.BadRequestException;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.exceptions.PermissionException;
import com.devanshu.lovableclone.mapper.ProjectMemberMapper;
import com.devanshu.lovableclone.repository.ProjectMemberRepository;
import com.devanshu.lovableclone.repository.ProjectsRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.ProjectMemberService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
	public List<ProjectMemberDTO> getProjectMembers(final Long projectId) {
		List<ProjectMember> projectMembers = projectMemberRepository.findByProjects_Id(projectId);
		return projectMemberMapper.toDtos(projectMembers);
	}

	@Override
	public ProjectMemberDTO inviteProjectMember(final Long projectId,
			final InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		if (inviteProjectMemberRequestDTO.username().equals(jwtUserPrincipal.username())) {
			throw new BadRequestException("User cannot invite themselves");
		}

		Users invitedUser = helperService.getUserByUsername(inviteProjectMemberRequestDTO.username());
		if (invitedUser.getDeletedAt() != null) {
			throw new BadRequestException(
					"User with username " + inviteProjectMemberRequestDTO.username() + " is inactive");
		}
		if (projectMemberRepository.existsByProjects_IdAndUsers_Id(projectId, invitedUser.getId())) {
			throw new BadRequestException("User is already a member of the project");
		}
		/**
		 * Currently only project owner can invite other user for contribution
		 */
		if (!projectMemberRepository.existsByProjects_IdAndUsers_IdAndProjectRole(projectId, jwtUserPrincipal.id(),
				ProjectRole.OWNER)) {
			throw new PermissionException("User is not the owner of the project");
		}
		Projects projects = getProject(projectId);
		ProjectMember projectMember = ProjectMember.builder().users(invitedUser).projects(projects)
				.id(new ProjectMemberId(invitedUser.getId(), projectId))
				.projectRole(inviteProjectMemberRequestDTO.projectRole()).status(ProjectMemberStatus.PENDING)
				.invitedAt(Instant.now()).build();
		projectMember = projectMemberRepository.save(projectMember);
		return projectMemberMapper.toDto(projectMember);
	}

	@Override
	public ProjectMember getProjectOwner(final Projects projects) {
		return projectMemberRepository.findByProjectsAndProjectRole(projects, ProjectRole.OWNER)
				.orElseThrow(() -> new NotFoundException("Project owner not found"));
	}

	@Override
	public ProjectMemberDTO updateProjectMemberRole(final Long projectId, final Long projectMemberId,
			final String projectRole) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Projects projects = getProject(projectId);
		ProjectMember projectOwner = getProjectOwner(projects);
		if (!Objects.equals(projectOwner.getUsers().getId(), jwtUserPrincipal.id())) {
			throw new PermissionException("User is not the owner of the project");
		}
		ProjectMember projectMember = projectMemberRepository.findById(new ProjectMemberId(projectMemberId, projectId))
				.orElseThrow(() -> new NotFoundException("Project member not found with id: " + projectMemberId));
		projectMember.setProjectRole(ProjectRole.valueOf(projectRole));
		projectMember = projectMemberRepository.save(projectMember);
		return projectMemberMapper.toDto(projectMember);
	}

	@Override
	public void removeProjectMember(final Long projectId, final Long projectMemberId) {
		ProjectMember projectMember = projectMemberRepository.findById(new ProjectMemberId(projectMemberId, projectId))
				.orElseThrow(() -> new NotFoundException("Project member not found with id: " + projectMemberId));

		projectMemberRepository.delete(projectMember);
	}

	@Override
	public void acceptRejectInviteProjectMember(final Long projectId, final String status) {
		if (!ProjectMemberStatus.ACCEPTED.name().equals(status)
				&& !ProjectMemberStatus.REJECTED.name().equals(status)) {
			throw new BadRequestException("Invalid status requested");
		}
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		ProjectMember projectMember = projectMemberRepository
				.findById(new ProjectMemberId(jwtUserPrincipal.id(), projectId))
				.orElseThrow(() -> new NotFoundException("User is not invited"));
		if (!ProjectMemberStatus.PENDING.equals(projectMember.getStatus())) {
			throw new BadRequestException("User has already responded to the invitation");
		}
		projectMember.setStatus(ProjectMemberStatus.valueOf(status));
		projectMemberRepository.save(projectMember);
	}

	private Projects getProject(final Long id) {
		return projectsRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Project not found with id: " + id));
	}
}
