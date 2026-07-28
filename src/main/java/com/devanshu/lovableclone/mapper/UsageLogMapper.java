package com.devanshu.lovableclone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.dto.usage.TodayUsageResponseDTO;
import com.devanshu.lovableclone.entity.UsageLog;

@Mapper(componentModel = "spring")
public interface UsageLogMapper {

	@Mapping(target = "tokenLimit", source = "subscriptionResponse.planResponseListDTO.maxTokensPerDay")
	@Mapping(target = "tokenUsed", source = "usageLog.tokenUsed")
	@Mapping(target = "previewRunning", source = "previewRunning")
	@Mapping(target = "previewLimit", source = "subscriptionResponse.planResponseListDTO.maxPreviews")
	TodayUsageResponseDTO toTodayUsageResponseDTO(UsageLog usageLog, SubscriptionResponseDTO subscriptionResponse,
			Integer previewRunning);

}
