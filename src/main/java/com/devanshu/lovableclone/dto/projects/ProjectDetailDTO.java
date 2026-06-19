package com.devanshu.lovableclone.dto.projects;

import java.time.Instant;

public record ProjectDetailDTO(Long id, String name, String description, Instant createdAt,
                               Instant updatedAt) {
}
