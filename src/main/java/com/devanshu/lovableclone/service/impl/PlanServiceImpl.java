package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.entity.Plan;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.mapper.PlanMapper;
import com.devanshu.lovableclone.repository.PlanRepository;
import com.devanshu.lovableclone.service.PlanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlanServiceImpl implements PlanService {

    PlanRepository planRepository;
    PlanMapper planMapper;

    @Override
    public List<PlanResponseListDTO> getPlans() {
        return planMapper.toDtos(planRepository.findAll());
    }

    @Override
    public Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                             .orElseThrow(() -> new NotFoundException("Plan " + planId));

    }
}
