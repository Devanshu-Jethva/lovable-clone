package com.devanshu.lovableclone.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

	@Bean
	ChatClient chatClient(final ChatClient.Builder chatClientBuilder) {
		return chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
	}
}
