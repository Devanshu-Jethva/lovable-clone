package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.ApiResponse;
import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.service.PaymentProcessor;
import com.devanshu.lovableclone.service.PlanService;
import com.devanshu.lovableclone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final PaymentProcessor paymentProcessor;

    @GetMapping("/plan")
    public ResponseEntity<Object> getPlans() {
        List<PlanResponseListDTO> planResponseListDTOS = planService.getPlans();
        return ResponseEntity.ok(ApiResponse.<List<PlanResponseListDTO>>builder()
                                            .message("Plans retrieved successfully")
                                            .data(planResponseListDTOS)
                                            .build());
    }

    @GetMapping("/subscription")
    public ResponseEntity<Object> getSubscription() {
        SubscriptionResponseDTO subscriptionResponseDTO = subscriptionService.getCurrentSubscription();
        return ResponseEntity.ok(ApiResponse.<SubscriptionResponseDTO>builder()
                                            .message("Subscription retrieved successfully")
                                            .data(subscriptionResponseDTO)
                                            .build());
    }

    @PostMapping("/payments/checkout")
    public ResponseEntity<Object> getStripeCheckout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        String checkoutUrl = paymentProcessor.createPaymentCheckoutUrl(checkoutRequestDTO);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(ApiResponse.<String>builder()
                                                                           .message("Checkout URL retrieved successfully")
                                                                           .data(checkoutUrl)
                                                                           .build());
    }

    @GetMapping("/payments/portal")
    public ResponseEntity<Object> getStripePortalUrl() {
        String portalUrl = paymentProcessor.getPaymentPortalUrl();
        return ResponseEntity.ok(ApiResponse.<String>builder()
                                            .message("Portal URL retrieved successfully")
                                            .data(portalUrl)
                                            .build());
    }
}
