package com.devanshu.lovableclone.dto.projects.member;

import java.time.Instant;

import com.devanshu.lovableclone.constant.ProjectRole;

public record ProjectMemberDTO(Long userId, String name, String username, ProjectRole projectRole, Instant invitedAt) {
}
