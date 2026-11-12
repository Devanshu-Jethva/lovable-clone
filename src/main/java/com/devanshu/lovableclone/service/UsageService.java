package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.usage.TodayUsageResponseDTO;

public interface UsageService {

	void recordTokenUsage(Long id, Integer totalTokens);

	void checkDailyTokensUsage();

	TodayUsageResponseDTO getTodayUsage();
}
