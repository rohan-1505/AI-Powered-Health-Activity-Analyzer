package com.fitness.gateway.user;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

	@Bean
	@LoadBalanced
	public WebClient.Builder webclientBuilder(){
		return WebClient.builder();
	}
	
	@Bean
	public WebClient userServiceWebCllient(WebClient.Builder webclient) {
		return webclientBuilder().baseUrl("http://USER-SERVICE")
				.build();
	}
}
	