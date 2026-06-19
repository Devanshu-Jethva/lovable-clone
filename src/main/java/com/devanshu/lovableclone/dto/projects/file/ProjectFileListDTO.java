package com.devanshu.lovableclone.dto.projects.file;

import java.time.Instant;

public record ProjectFileListDTO(
        String path,
        Instant updateAt,
        Long size,
        String type
) {
}
