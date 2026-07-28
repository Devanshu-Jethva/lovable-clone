package com.devanshu.lovableclone.config;

import java.time.Duration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openai.core.Timeout;

@Configuration
public class AiConfig {

	@Bean
	ChatClient chatClient(final ChatClient.Builder chatClientBuilder) {
		return chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
	}

	@Bean
	OpenAiHttpClientBuilderCustomizer openAiHttpClientBuilderCustomizer() {
		return builder -> builder.timeout(Timeout.builder().connect(Duration.ofSeconds(500))
				.read(Duration.ofSeconds(500)).write(Duration.ofSeconds(500)).request(Duration.ofSeconds(500)).build());
	}
}
