package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.service.PlanService;
import com.devanshu.lovableclone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final PlanService planService;

    private final SubscriptionService subscriptionService;

    @GetMapping("/api/plan")
    public ResponseEntity<Object> getPlans() {
        List<PlanResponseListDTO> planResponseListDTOS = planService.getPlans();
        return ResponseEntity.ok(ApiResponse.<List<PlanResponseListDTO>>builder()
                                            .message("Plans retrieved successfully")
                                            .data(planResponseListDTOS)
                                            .build());
    }

    @GetMapping("/api/subscription")
    public ResponseEntity<Object> getSubscription() {
        SubscriptionResponseDTO subscriptionResponseDTO = subscriptionService.getCurrentSubscription();
        return ResponseEntity.ok(ApiResponse.<SubscriptionResponseDTO>builder()
                                            .message("Subscription retrieved successfully")
                                            .data(subscriptionResponseDTO)
                                            .build());
    }

    @PostMapping("/api/stripe/checkout")
    public ResponseEntity<Object> getStripeCheckout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        String checkoutUrl = subscriptionService.getStripeCheckoutUrl(checkoutRequestDTO);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                                            .message("Checkout URL retrieved successfully")
                                            .data(checkoutUrl)
                                            .build());
    }

    @GetMapping("/api/stripe/portal")
    public ResponseEntity<Object> getStripePortalUrl() {
        String portalUrl = subscriptionService.getStripePortalUrl();
        return ResponseEntity.ok(ApiResponse.<String>builder()
                                            .message("Portal URL retrieved successfully")
                                            .data(portalUrl)
                                            .build());
    }
}
