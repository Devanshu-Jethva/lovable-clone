package com.devanshu.lovableclone.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtPropertiesConfig {

    @NotBlank
    private String secret;
}
