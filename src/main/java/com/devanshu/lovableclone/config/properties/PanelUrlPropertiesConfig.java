package com.devanshu.lovableclone.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "panel-url")
public class PanelUrlPropertiesConfig {
    
    @NotBlank
    private String admin;
}