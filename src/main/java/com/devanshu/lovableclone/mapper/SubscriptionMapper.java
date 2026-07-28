package com.devanshu.lovableclone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.entity.Subscription;

@Mapper(componentModel = "spring", uses = PlanMapper.class)
public interface SubscriptionMapper {

	@Mapping(source = "plan", target = "planResponseListDTO")
	@Mapping(target = "tokenUsed", ignore = true)
	SubscriptionResponseDTO toDto(Subscription currentSubscription);

}
