package com.devanshu.lovableclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devanshu.lovableclone.config.properties.StoragePropertiesConfig;

import io.minio.MinioClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Configuration
@RequiredArgsConstructor
public class StorageConfig {
	private final StoragePropertiesConfig storagePropertiesConfig;

	@Bean
	MinioClient minioClient() {
		return MinioClient.builder().endpoint(storagePropertiesConfig.getUrl())
				.credentials(storagePropertiesConfig.getAccessKey(), storagePropertiesConfig.getSecretKey()).build();
	}
}
