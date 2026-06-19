package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponseListDTO> getPlans() {
        return List.of();
    }
}
