package com.devanshu.lovableclone.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devanshu.lovableclone.config.properties.StripePropertiesConfig;
import com.devanshu.lovableclone.service.PaymentProcessor;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class PaymentWebhookController {

	private final StripePropertiesConfig stripePropertiesConfig;
	private final PaymentProcessor paymentProcessor;

	@PostMapping
	public ResponseEntity<String> payments(@RequestBody final String payload,
			@RequestHeader("Stripe-Signature") final String stripeSignature) {
		try {
			Event event = Webhook.constructEvent(payload, stripeSignature,
					stripePropertiesConfig.getWebhook().getSecret());
			EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
			StripeObject stripeObject;
			if (deserializer.getObject().isPresent()) {
				stripeObject = deserializer.getObject().get();
			} else {
				// Fallback: deserialize from JSON
				try {
					stripeObject = deserializer.deserializeUnsafe();
					if (stripeObject == null) {
						log.warn("Failed to deserialize object from event: {}", event.getType());
						return ResponseEntity.ok().build();
					}
				} catch (Exception e) {
					log.error("Unsafe deserialization failed for event: {} | {}", event.getType(), e.getMessage());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
				}
			}
			// Now extract metadata only if it's checkout session
			Map<String, String> metadata = new HashMap<>();
			if (stripeObject instanceof Session session) {
				metadata = session.getMetadata();
			}

			// pass to you processor
			paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);

			return ResponseEntity.ok().build();
		} catch (SignatureVerificationException e) {
			log.warn("Invalid Stripe signature");
			return ResponseEntity.badRequest().body("Invalid signature");
		}
	}
}
