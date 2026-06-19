package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.projects.member.InviteProjectMemberRequestDTO;
import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.service.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {
    @Override
    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        return List.of();
    }

    @Override
    public void inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {

    }

    @Override
    public void updateProjectMemberRole(Long projectId, Long id, String projectRole) {

    }

    @Override
    public void deleteProjectMember(Long projectId, Long id) {

    }
}
