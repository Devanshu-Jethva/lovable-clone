package com.devanshu.lovableclone.dto.projects.member;

import com.devanshu.lovableclone.constant.ProjectRole;

import java.time.Instant;

public record ProjectMemberDTO(
        Long userId, String name, String username,
        ProjectRole projectRole, Instant invitedAt
) {
}
