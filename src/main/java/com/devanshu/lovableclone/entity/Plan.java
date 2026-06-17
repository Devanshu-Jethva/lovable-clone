package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Data
@Entity
@Table(name = "plan")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan extends CommonModel {

    @Serial
    private static final long serialVersionUID = -1737813184984828118L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /*
     * can be something like free, pro, ultimate, etc.
     */
    @Column(name = "name", nullable = false)
    String name;

    /*
     * This is the price ID from Stripe, which we will use to identify the plan when creating subscriptions.
     * Using this we will be able to identify what is pricing for this plan
     */
    @Column(name = "stripe_price_id")
    String stripPriceId;

    /*
     * The maximum number of projects a user can have under this plan.
     */
    @Column(name = "max_projects")
    Integer maxProjects;

    /*
     * The maximum number of tokens a user can use per day under this plan.
     */
    @Column(name = "max_tokens_per_day")
    Long maxTokensPerDay;

    /*
     * The maximum number of allowed previews
     */
    @Column(name = "max_previews")
    Integer maxPreviews;

    /*
     * Indicates whether the user has unlimited access to AI features under this plan.
     * Ignores maxTokensPerDay if true
     */
    @Column(name = "unlimited_ai")
    Boolean unlimitedAi;

    /*
     * Indicates whether the plan is currently active.
     */
    @Column(name = "active")
    Boolean active;
}
