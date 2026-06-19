package com.devanshu.lovableclone.dto.plan;

public record PlanResponseListDTO(
        Long id,
        String name,
        Integer maxProjects,
        Long maxTokensPerDay,
        Integer maxPreviews,
        Boolean unlimitedAi,
        Long price,
        Boolean active
) {
}
