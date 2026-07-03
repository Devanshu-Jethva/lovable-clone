package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    String createPaymentCheckoutUrl(CheckoutRequestDTO checkoutRequestDTO);

    String getPaymentPortalUrl();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
