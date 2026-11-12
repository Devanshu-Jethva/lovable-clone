package com.devanshu.lovableclone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.service.ProjectMemberService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/member")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberController {

	ProjectMemberService projectMemberService;

	@PostMapping("/invite")
	@PreAuthorize("@securityExpression.canManageProjectMembers(#projectId)")
	public ResponseEntity<Object> inviteProjectMember(@PathVariable final Long projectId,
			@Valid @RequestBody final InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
		ProjectMemberDTO projectMemberDTO = projectMemberService.inviteProjectMember(projectId,
				inviteProjectMemberRequestDTO);
		return ResponseEntity.ok(ApiResponse.<ProjectMemberDTO>builder().data(projectMemberDTO)
				.message("Project member invited successfully").build());
	}

	@PostMapping("/accept-reject")
	public ResponseEntity<Object> acceptRejectInviteProjectMember(@PathVariable final Long projectId,
			@RequestParam final String status) {
		projectMemberService.acceptRejectInviteProjectMember(projectId, status);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(ApiResponse.builder().message("Project member invitation accepted successfully").build());
	}

	@PatchMapping("/{projectMemberId}")
	@PreAuthorize("@securityExpression.canManageProjectMembers(#projectId)")
	public ResponseEntity<Object> updateProjectMemberRole(@PathVariable final Long projectId,
			@PathVariable final Long projectMemberId, @RequestParam final String projectRole) {
		ProjectMemberDTO projectMemberDTO = projectMemberService.updateProjectMemberRole(projectId, projectMemberId,
				projectRole);
		return ResponseEntity.ok(ApiResponse.<ProjectMemberDTO>builder().data(projectMemberDTO)
				.message("Project member role updated successfully").build());
	}

	@GetMapping
	@PreAuthorize("@securityExpression.canViewProjectMembers(#projectId)")
	public ResponseEntity<Object> getProjectMembers(@PathVariable final Long projectId) {
		List<ProjectMemberDTO> projectMemberDTOS = projectMemberService.getProjectMembers(projectId);
		return ResponseEntity.ok(ApiResponse.<List<ProjectMemberDTO>>builder().data(projectMemberDTOS)
				.message("Project members fetched successfully").build());
	}

	@DeleteMapping("/{projectMemberId}")
	@PreAuthorize("@securityExpression.canManageProjectMembers(#projectId)")
	public ResponseEntity<Object> removeProjectMember(@PathVariable final Long projectId,
			@PathVariable final Long projectMemberId) {
		projectMemberService.removeProjectMember(projectId, projectMemberId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(ApiResponse.builder().message("Project member deleted successfully").build());
	}
}
