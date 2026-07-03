package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.entity.Plan;

import java.util.List;

public interface PlanService {

    List<PlanResponseListDTO> getPlans();

    Plan getPlan(Long planId);
}
