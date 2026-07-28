package com.devanshu.lovableclone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.usage.TodayUsageResponseDTO;
import com.devanshu.lovableclone.service.UsageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usage")
public class UsageController {

	private final UsageService usageService;

	@GetMapping("/today")
	public ResponseEntity<Object> getTodayUsage() {
		TodayUsageResponseDTO todayUsageResponseDTO = usageService.getTodayUsage();
		return ResponseEntity.ok(ApiResponse.<TodayUsageResponseDTO>builder()
				.message("Today usage retrieved successfully").data(todayUsageResponseDTO).build());
	}

}
