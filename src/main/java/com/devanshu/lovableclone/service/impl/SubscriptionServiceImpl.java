package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;
import com.devanshu.lovableclone.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponseDTO getCurrentSubscription() {
        return null;
    }

    @Override
    public String getStripeCheckoutUrl(CheckoutRequestDTO checkoutRequestDTO) {
        return "";
    }

    @Override
    public String getStripePortalUrl() {
        return "";
    }
}
