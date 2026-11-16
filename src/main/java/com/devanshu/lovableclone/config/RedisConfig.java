package com.devanshu.lovableclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

	@Bean
	StringRedisTemplate redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
}
