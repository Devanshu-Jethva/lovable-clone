package com.devanshu.lovableclone.dto.projects.file;

import java.time.Instant;

public record ProjectFileDetailDTO(
        Long id,
        String path,
        Instant updateAt,
        String content
) {
}
