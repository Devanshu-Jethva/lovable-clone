package com.devanshu.lovableclone.dto.projects;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequestDTO(
        Long id,
        @NotBlank(message = "Name is required") String name,
        String description) {
}
