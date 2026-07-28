package com.devanshu.lovableclone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
	Optional<Plan> findByStripePriceId(String id);
}
