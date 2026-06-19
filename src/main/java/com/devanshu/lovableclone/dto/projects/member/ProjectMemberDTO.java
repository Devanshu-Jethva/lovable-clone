package com.devanshu.lovableclone.dto.projects.member;

import com.devanshu.lovableclone.constant.ProjectRole;

import java.time.Instant;

public record ProjectMemberDTO(
        Long id, String name, String email, String avatarUrl,
        ProjectRole projectRole, Instant invitedAt
) {
}
