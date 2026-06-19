package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;

import java.util.List;

public interface ProjectMemberService {
    
    List<ProjectMemberDTO> getProjectMembers(Long projectId);

    void inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO);

    void updateProjectMemberRole(Long projectId, Long id, String projectRole);

    void deleteProjectMember(Long projectId, Long id);
}
