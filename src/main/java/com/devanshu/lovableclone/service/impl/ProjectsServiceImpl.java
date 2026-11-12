package com.devanshu.lovableclone.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.ProjectMemberService;
import com.devanshu.lovableclone.service.ProjectTemplateService;
import com.devanshu.lovableclone.service.ProjectsService;
import com.devanshu.lovableclone.service.SubscriptionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectsServiceImpl implements ProjectsService {

	ProjectsRepository projectsRepository;
	UsersRepository usersRepository;
	ProjectsMapper projectsMapper;
	ProjectMemberRepository projectMemberRepository;
	ProjectMemberService projectMemberService;
	HelperService helperService;
	SubscriptionService subscriptionService;
	ProjectTemplateService projectTemplateService;

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public ProjectDetailDTO createProject(final ProjectRequestDTO projectRequestDTO) {
		if (!subscriptionService.canCreateNewProject()) {
			throw new BadRequestException("Subscription limit reached, Can't create new project anymore.");
		}

		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Users users = usersRepository.getReferenceById(jwtUserPrincipal.id());

		Projects projects = Projects.builder().name(projectRequestDTO.name())
				.description(projectRequestDTO.description()).build();
		projects = projectsRepository.save(projects);

		ProjectMember projectMember = ProjectMember.builder()
				.id(new ProjectMemberId(jwtUserPrincipal.id(), projects.getId())).projects(projects).users(users)
				.projectRole(ProjectRole.OWNER).invitedAt(Instant.now()).status(ProjectMemberStatus.ACCEPTED).build();
		projectMemberRepository.save(projectMember);

		projectTemplateService.initializeProjectFromTemplate(projects.getId());

		return projectsMapper.toDetailDto(projects);
	}

	@Override
	public List<ProjectListDTO> getAllProjects() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		List<Projects> projects = projectsRepository.getAllAccessibleProjects(jwtUserPrincipal.id());
		return projectsMapper.toProjectListDTOs(projects);
	}

	@Override
	public ProjectDetailDTO getProjectById(final Long id) {
		return projectsMapper.toDetailDto(getProject(id));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public ProjectDetailDTO updateProject(final ProjectRequestDTO projectRequestDTO) {
		if (projectRequestDTO.id() == null) {
			throw new BadRequestException("Project id is required for update");
		}
		Projects projects = getProject(projectRequestDTO.id());
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		ProjectMember projectOwner = projectMemberService.getProjectOwner(projects);
		if (!Objects.equals(projectOwner.getUsers().getId(), jwtUserPrincipal.id())) {
			throw new PermissionException("User is not the owner of the project");
		}
		if (projects.getDeletedAt() != null) {
			throw new BadRequestException("Project is deleted and cannot be updated");
		}
		projects.setName(projectRequestDTO.name());
		projects.setDescription(projectRequestDTO.description());
		projects = projectsRepository.save(projects);
		return projectsMapper.toDetailDto(projects);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteProject(final Long id) {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Projects projects = getProject(id);
		ProjectMember projectOwner = projectMemberService.getProjectOwner(projects);
		if (!Objects.equals(projectOwner.getUsers().getId(), jwtUserPrincipal.id())) {
			throw new PermissionException("User is not the owner of the project");
		}
		if (projects.getDeletedAt() != null) {
			throw new BadRequestException("Project is already deleted");
		}
		projects.setDeletedAt(Instant.now());
		projectsRepository.save(projects);
	}

	@Override
	public Projects getProject(final Long id) {
		return projectsRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Project with " + id + " not found"));
	}

}
