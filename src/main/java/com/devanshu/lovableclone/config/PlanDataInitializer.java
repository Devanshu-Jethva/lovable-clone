package com.devanshu.lovableclone.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.devanshu.lovableclone.entity.Plan;
import com.devanshu.lovableclone.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlanDataInitializer implements CommandLineRunner {

	private final PlanRepository planRepository;

	@Override
	public void run(final String... args) {

		if (planRepository.count() > 0) {
			return;
		}

		planRepository.saveAll(List.of(
				Plan.builder().name("Pro Plan").stripePriceId("price_1TnKKF2QsNpTpY3hJWP5Ym7I").price(499.0)
						.maxProjects(3).maxTokensPerDay(10_000L).maxPreviews(1).unlimitedAi(true).active(true).build(),

				Plan.builder().name("Business Plan").stripePriceId("price_1TnIcF2QsNpTpY3hcK7SVnr0").price(1499.00)
						.maxProjects(10).maxTokensPerDay(50_000L).maxPreviews(3).unlimitedAi(true).active(true)
						.build()));
	}
}