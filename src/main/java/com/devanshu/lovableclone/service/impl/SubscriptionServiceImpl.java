package com.devanshu.lovableclone.service.impl;

import java.time.Instant;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devanshu.lovableclone.constant.ApplicationConstants;
import com.devanshu.lovableclone.constant.SubscriptionStatus;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.entity.Plan;
import com.devanshu.lovableclone.entity.Subscription;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.mapper.SubscriptionMapper;
import com.devanshu.lovableclone.repository.ProjectMemberRepository;
import com.devanshu.lovableclone.repository.SubscriptionRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.PlanService;
import com.devanshu.lovableclone.service.SubscriptionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {

	SubscriptionRepository subscriptionRepository;
	SubscriptionMapper subscriptionMapper;
	ProjectMemberRepository projectMemberRepository;
	HelperService helperService;
	PlanService planService;

	@Override
	public SubscriptionResponseDTO getCurrentSubscription() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();

		Subscription currentSubscription = subscriptionRepository
				.findByUsersIdAndStatusIn(jwtUserPrincipal.id(), SubscriptionStatus.getActiveSubscriptionStatusList())
				.orElseThrow(() -> new NotFoundException("No active subscription found."));

		return subscriptionMapper.toDto(currentSubscription);
	}

	@Override
	public boolean canCreateNewProject() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		SubscriptionResponseDTO currentSubscription = getCurrentSubscription();

		int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(jwtUserPrincipal.id());

		if (currentSubscription.planResponseListDTO() == null) {
			return countOfOwnedProjects < ApplicationConstants.FREE_TIER_PROJECTS_ALLOWED;
		}

		return countOfOwnedProjects < currentSubscription.planResponseListDTO().maxProjects();
	}

	@Override
	@Transactional
	public void activateSubscription(final Long userId, final Long planId, final String subscriptionId,
			final String stripeCustomerId) {
		if (subscriptionRepository.existsByStripeSubscriptionId(subscriptionId)) {
			log.error("Subscription already exists");
			return;
		}

		Users users = helperService.getUser(userId);
		Plan plan = planService.getPlan(planId);

		Subscription subscription = Subscription.builder().users(users).plan(plan).stripeSubscriptionId(subscriptionId)
				.status(SubscriptionStatus.INCOMPLETE).build();

		subscriptionRepository.save(subscription);
	}

	@Override
	@Transactional
	public void updateSubscription(final String stripeSubscriptionId, final SubscriptionStatus status,
			final Instant periodStart, final Instant periodEnd, final Boolean cancelAtPeriodEnd, final Long planId) {
		Subscription subscription = getSubscription(stripeSubscriptionId);

		boolean hasSubscriptionUpdated = false;

		if (status != null && status != subscription.getStatus()) {
			subscription.setStatus(status);
			hasSubscriptionUpdated = true;
		}

		if (periodStart != null && !periodStart.equals(subscription.getStartDate())) {
			subscription.setStartDate(periodStart);
			hasSubscriptionUpdated = true;
		}

		if (periodEnd != null && !periodEnd.equals(subscription.getEndDate())) {
			subscription.setEndDate(periodEnd);
			hasSubscriptionUpdated = true;
		}

		if (cancelAtPeriodEnd != null && !Objects.equals(cancelAtPeriodEnd, subscription.getCancelAtPeriodEnd())) {
			subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
			hasSubscriptionUpdated = true;
		}

		if (planId != null && !planId.equals(subscription.getPlan().getId())) {
			Plan newPlan = planService.getPlan(planId);
			subscription.setPlan(newPlan);
			hasSubscriptionUpdated = true;
		}

		if (hasSubscriptionUpdated) {
			log.debug("Subscription has been updated: {}", stripeSubscriptionId);
			subscriptionRepository.save(subscription);
		}
	}

	@Override
	public void cancelSubscription(final String stripeSubscriptionId) {
		Subscription subscription = getSubscription(stripeSubscriptionId);
		subscription.setStatus(SubscriptionStatus.CANCELED);
		subscriptionRepository.save(subscription);
	}

	@Override
	public void renewSubscriptionPeriod(final String stripeSubscriptionId, final Instant periodStart,
			final Instant periodEnd) {
		Subscription subscription = getSubscription(stripeSubscriptionId);

		Instant newStart = periodStart != null ? periodStart : subscription.getEndDate();
		subscription.setStartDate(newStart);
		subscription.setEndDate(periodEnd);

		if (subscription.getStatus() == SubscriptionStatus.PAST_DUE
				|| subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
			subscription.setStatus(SubscriptionStatus.ACTIVE);
		}

		subscriptionRepository.save(subscription);
	}

	@Override
	public void markSubscriptionPastDue(final String stripeSubscriptionId) {
		Subscription subscription = getSubscription(stripeSubscriptionId);

		if (SubscriptionStatus.PAST_DUE.equals(subscription.getStatus())) {
			log.debug("Subscription is already past due, stripeSubscriptionId: {}", stripeSubscriptionId);
			return;
		}

		subscription.setStatus(SubscriptionStatus.PAST_DUE);
		subscriptionRepository.save(subscription);

		// Notify user via email..
	}

	private Subscription getSubscription(final String stripeSubscriptionId) {
		return subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
				.orElseThrow(() -> new NotFoundException("Subscription " + stripeSubscriptionId));
	}

}
