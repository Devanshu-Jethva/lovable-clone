package com.devanshu.lovableclone.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "stripe")
public class StripePropertiesConfig {

    private Api api;

    private Webhook webhook;

    @Data
    public static class Api {
        @NotBlank
        private String secret;
    }

    @Data
    public static class Webhook {
        @NotBlank
        private String secret;
    }
}
