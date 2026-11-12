package com.devanshu.lovableclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devanshu.lovableclone.config.properties.StripePropertiesConfig;
import com.stripe.StripeClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PaymentConfig {

	private final StripePropertiesConfig stripePropertiesConfig;

	@Bean
	StripeClient stripeClient() {
//        return new StripeClient(stripePropertiesConfig.getSecret());
		return StripeClient.builder().setApiKey(stripePropertiesConfig.getApi().getSecret()).build();
	}

}
