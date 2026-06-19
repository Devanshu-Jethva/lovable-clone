package com.devanshu.lovableclone.entity;

import com.devanshu.lovableclone.constant.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscription")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription extends CommonModel {

    @Serial
    private static final long serialVersionUID = 425887524527835986L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    Plan plan;

    @Column(name = "stripe_customer_id", nullable = false)
    String stripeCustomerId;

    @Column(name = "stripe_subscription_id", nullable = false)
    String stripeSubscriptionId;

    @Column(name = "start_date", nullable = false)
    Instant startDate;

    @Column(name = "end_date")
    Instant endDate;

    @Column(name = "cancelled_date")
    Instant cancelledDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    SubscriptionStatus status;
}
