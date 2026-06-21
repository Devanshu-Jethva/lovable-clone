package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectMemberDTO> getProjectMembers(Long projectId);

    ProjectMemberDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO);

    ProjectMemberDTO updateProjectMemberRole(Long projectId, Long projectMemberId, String projectRole);

    void deleteProjectMember(Long projectId, Long projectMemberId);

    void acceptRejectInviteProjectMember(Long projectId, String status);
}
