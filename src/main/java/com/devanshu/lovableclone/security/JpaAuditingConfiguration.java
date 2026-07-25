package com.devanshu.lovableclone.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.devanshu.lovableclone.config.UsernameAuditorAware;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {

	@Bean
	UsernameAuditorAware auditorProvider() {
		return new UsernameAuditorAware();
	}
}
