package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.GenericResponseHandler;
import com.devanshu.lovableclone.dto.usage.TodayUsageResponseDTO;
import com.devanshu.lovableclone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<Object> getTodayUsage() {
        TodayUsageResponseDTO todayUsageResponseDTO = usageService.getTodayUsage();
        return GenericResponseHandler.builder().status(HttpStatus.OK).message("Today usage retrieved successfully")
                                     .data(todayUsageResponseDTO).build();
    }

}
