package com.devanshu.lovableclone.dto.subscription;

import java.time.Instant;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;

public record SubscriptionResponseDTO(PlanResponseListDTO planResponseListDTO, String status, Instant endDate,
		Long tokenUsed) {
}
