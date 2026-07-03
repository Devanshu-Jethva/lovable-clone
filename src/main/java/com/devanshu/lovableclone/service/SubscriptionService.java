package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.constant.SubscriptionStatus;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponseDTO getCurrentSubscription();

    boolean canCreateNewProject();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String stripeCustomerId);

    void updateSubscription(String stripeSubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String stripeSubscriptionId);

    void renewSubscriptionPeriod(String stripeSubscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String stripeSubscriptionId);
}
