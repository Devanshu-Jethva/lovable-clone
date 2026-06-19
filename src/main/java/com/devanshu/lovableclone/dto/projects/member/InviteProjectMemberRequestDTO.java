package com.devanshu.lovableclone.dto.projects.member;

import com.devanshu.lovableclone.constant.ProjectRole;

public record InviteProjectMemberRequestDTO(
        String email,
        ProjectRole projectRole
) {
}
