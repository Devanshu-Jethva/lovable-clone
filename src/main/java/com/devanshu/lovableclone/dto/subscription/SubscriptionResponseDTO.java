package com.devanshu.lovableclone.dto.subscription;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;

import java.time.Instant;

public record SubscriptionResponseDTO(
        PlanResponseListDTO planResponseListDTO,
        String status,
        Instant endDate,
        Long tokenUsed
) {
}
