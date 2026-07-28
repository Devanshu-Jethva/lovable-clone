package com.devanshu.lovableclone.service.impl;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.dto.usage.TodayUsageResponseDTO;
import com.devanshu.lovableclone.entity.UsageLog;
import com.devanshu.lovableclone.mapper.UsageLogMapper;
import com.devanshu.lovableclone.repository.UsageLogRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.SubscriptionService;
import com.devanshu.lovableclone.service.UsageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

	private final HelperService helperService;
	private final UsageLogRepository usageLogRepository;
	private final SubscriptionService subscriptionService;
	private final UsageLogMapper usageLogMapper;

	@Override
	public void recordTokenUsage(final Long userId, final int actualTokens) {
		LocalDate today = LocalDate.now();

		UsageLog todayLog = usageLogRepository.findByUsersIdAndDate(userId, today)
				.orElseGet(() -> createNewDailyLog(userId, today));

		todayLog.setTokenUsed(todayLog.getTokenUsed() + actualTokens);
		usageLogRepository.save(todayLog);
	}

	@Override
	public void checkDailyTokensUsage() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Long userId = jwtUserPrincipal.id();
		SubscriptionResponseDTO subscriptionResponse = subscriptionService.getCurrentSubscription();
		PlanResponseListDTO planResponseListDTO = subscriptionResponse.planResponseListDTO();

		LocalDate today = LocalDate.now();

		UsageLog todayLog = usageLogRepository.findByUsersIdAndDate(userId, today)
				.orElseGet(() -> createNewDailyLog(userId, today));

		if (Boolean.TRUE.equals(planResponseListDTO.unlimitedAi())) {
			return;
		}

		Integer currentUsage = todayLog.getTokenUsed();
		Long limit = planResponseListDTO.maxTokensPerDay();

		if (currentUsage >= limit) {
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily limit reached, Upgrade now");
		}

	}

	private UsageLog createNewDailyLog(final Long usersId, final LocalDate date) {
		UsageLog newLog = UsageLog.builder().usersId(usersId).date(date).tokenUsed(0).build();
		return usageLogRepository.save(newLog);
	}

	@Override
	public TodayUsageResponseDTO getTodayUsage() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Long userId = jwtUserPrincipal.id();
		LocalDate today = LocalDate.now();

		UsageLog todayLog = usageLogRepository.findByUsersIdAndDate(userId, today)
				.orElseGet(() -> createNewDailyLog(userId, today));

		SubscriptionResponseDTO subscriptionResponse = subscriptionService.getCurrentSubscription();

		// TODO: set preview running
		return usageLogMapper.toTodayUsageResponseDTO(todayLog, subscriptionResponse, 0);
	}
}
