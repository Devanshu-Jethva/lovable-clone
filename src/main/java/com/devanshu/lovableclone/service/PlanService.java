package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;

import java.util.List;

public interface PlanService {
    
    List<PlanResponseListDTO> getPlans();
}
