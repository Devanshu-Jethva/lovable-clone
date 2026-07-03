package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<Object> getAllPlans() {
        List<PlanResponseListDTO> planResponseListDTOS = planService.getPlans();
        return ResponseEntity.ok(ApiResponse.<List<PlanResponseListDTO>>builder()
                                            .data(planResponseListDTOS)
                                            .message("Plans fetched successfully")
                                            .build());
    }
}
