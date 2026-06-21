package com.devanshu.lovableclone.dto.projects.member;

import com.devanshu.lovableclone.constant.ProjectRole;

import java.time.Instant;

public record ProjectMemberDTO(
        Long userId, String name, String email,
        ProjectRole projectRole, Instant invitedAt
) {
}
