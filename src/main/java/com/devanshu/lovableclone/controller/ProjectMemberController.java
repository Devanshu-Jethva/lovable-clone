package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.GenericResponseHandler;
import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/member")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberController {

    ProjectMemberService projectMemberService;

    @PostMapping("/invite")
    public ResponseEntity<Object> inviteProjectMember(@PathVariable Long projectId, @RequestBody InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        projectMemberService.inviteProjectMember(projectId, inviteProjectMemberRequestDTO);
        return GenericResponseHandler.builder().message("Project member invited successfully").status(HttpStatus.OK)
                                     .build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateProjectMemberRole(@PathVariable Long projectId, @PathVariable Long id, @RequestParam String projectRole) {
        projectMemberService.updateProjectMemberRole(projectId, id, projectRole);
        return GenericResponseHandler.builder().message("Project member role updated successfully")
                                     .status(HttpStatus.OK)
                                     .build();
    }

    @GetMapping
    public ResponseEntity<Object> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMemberDTO> projectMemberDTOS = projectMemberService.getProjectMembers(projectId);
        return GenericResponseHandler.builder()
                                     .data(projectMemberDTOS)
                                     .message("Project members fetched successfully")
                                     .status(HttpStatus.OK)
                                     .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProjectMember(@PathVariable Long projectId, @PathVariable Long id) {
        projectMemberService.deleteProjectMember(projectId, id);
        return GenericResponseHandler.builder().message("Project member deleted successfully").status(HttpStatus.OK)
                                     .build();
    }
}
