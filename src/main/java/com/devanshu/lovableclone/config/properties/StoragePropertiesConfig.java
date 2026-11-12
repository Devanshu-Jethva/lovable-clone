package com.devanshu.lovableclone.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class StoragePropertiesConfig {

	@NotBlank
	private String url;
	@NotBlank
	private String accessKey;
	@NotBlank
	private String secretKey;
	@NotBlank
	private String projectBucket;
}
