package com.devanshu.lovableclone.constant;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public enum SubscriptionStatus {
    TRIALING,
    ACTIVE,
    PAST_DUE,
    CANCELED,
    INCOMPLETE;

    private final static Set<SubscriptionStatus> ACTIVE_SUBSCRIPTION_STATUS = Set.of(ACTIVE, PAST_DUE, TRIALING);

    public static SubscriptionStatus toEnum(String status) {
        return switch (status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRIALING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    public static Set<SubscriptionStatus> getActiveSubscriptionStatusList() {
        return ACTIVE_SUBSCRIPTION_STATUS;
    }
}
