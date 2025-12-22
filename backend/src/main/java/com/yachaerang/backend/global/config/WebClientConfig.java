package com.yachaerang.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String OPEN_AI_BASE_URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/chat/completions";

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean(name="openAiWebClient")
    public WebClient openAiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(OPEN_AI_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
