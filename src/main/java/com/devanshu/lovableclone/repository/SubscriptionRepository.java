package com.devanshu.lovableclone.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.constant.SubscriptionStatus;
import com.devanshu.lovableclone.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	Optional<Subscription> findByUsersIdAndStatusIn(Long id, Set<SubscriptionStatus> active);

	boolean existsByStripeSubscriptionId(String subscriptionId);

	Optional<Subscription> findByStripeSubscriptionId(String gatewaySubscriptionId);
}
