package com.devanshu.lovableclone.dto.usage;

public record TodayUsageResponseDTO(
        Long tokenLimit,
        Long tokenUsed,
        Integer previewRunning,
        Integer previewLimit
) {
}
