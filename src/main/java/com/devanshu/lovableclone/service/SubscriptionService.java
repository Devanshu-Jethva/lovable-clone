package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.devanshu.lovableclone.dto.subscription.SubscriptionResponseDTO;

public interface SubscriptionService {
    SubscriptionResponseDTO getCurrentSubscription();

    String getStripeCheckoutUrl(CheckoutRequestDTO checkoutRequestDTO);

    String getStripePortalUrl();
}
