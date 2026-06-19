package com.devanshu.lovableclone.dto.projects;

import java.time.Instant;

public record ProjectListDTO(Long id, String name, Instant createdAt, Instant updatedAt) {
}
