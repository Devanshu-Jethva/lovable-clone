package com.devanshu.lovableclone.dto.projects.member;

import com.devanshu.lovableclone.constant.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteProjectMemberRequestDTO(
        @NotBlank(message = "Username is required") String username,
        @NotNull(message = "Project role is required") ProjectRole projectRole
) {
}
