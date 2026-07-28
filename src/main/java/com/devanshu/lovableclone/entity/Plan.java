package com.devanshu.lovableclone.entity;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@Table(name = "plan")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan extends CommonModel {

	@Serial
	private static final long serialVersionUID = -1737813184984828118L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	/**
	 * can be something like free, pro, ultimate, etc.
	 */
	@Column(name = "name", nullable = false)
	String name;

	/**
	 * This is the price ID from Stripe, which we will use to identify the plan when
	 * creating subscriptions. <br/>
	 * Using this we will be able to identify what is pricing for this plan
	 */
	@Column(name = "stripe_price_id", unique = true, nullable = false)
	String stripePriceId;

	@Column(name = "price", nullable = false)
	Double price;

	/**
	 * The maximum number of projects a user can have under this plan. <br/>
	 */
	@Column(name = "max_projects")
	Integer maxProjects;

	/**
	 * The maximum number of tokens a user can use per day under this plan. <br/>
	 */
	@Column(name = "max_tokens_per_day")
	Long maxTokensPerDay;

	/**
	 * The maximum number of allowed previews
	 */
	@Column(name = "max_previews")
	Integer maxPreviews;

	/**
	 * Indicates whether the user has unlimited access to AI features under this
	 * plan. <br/>
	 * Ignores maxTokensPerDay if true
	 */
	@Column(name = "unlimited_ai")
	Boolean unlimitedAi;

	/**
	 * Indicates whether the plan is currently active.
	 */
	@Column(name = "active")
	Boolean active;
}
