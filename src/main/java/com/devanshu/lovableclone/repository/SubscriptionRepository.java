package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.constant.SubscriptionStatus;
import com.devanshu.lovableclone.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUsersIdAndStatusIn(Long id, Set<SubscriptionStatus> active);

    boolean existsByStripeSubscriptionId(String subscriptionId);

    Optional<Subscription> findByStripeSubscriptionId(String gatewaySubscriptionId);
}
