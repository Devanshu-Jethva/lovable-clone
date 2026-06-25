package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.Projects;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectMemberDTO> getProjectMembers(Long projectId);

    ProjectMemberDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO);

    ProjectMember getProjectOwner(Projects projects);

    ProjectMemberDTO updateProjectMemberRole(Long projectId, Long projectMemberId, String projectRole);

    void deleteProjectMember(Long projectId, Long projectMemberId);

    void acceptRejectInviteProjectMember(Long projectId, String status);
}
