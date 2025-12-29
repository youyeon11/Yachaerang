package com.yachaerang.backend.global.config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleGenAiConfig {

    private final static String BASE_URL = "https://chat.googleapis.com";

    @Bean
    public GoogleGenAiChatModel googleGenAiChatModel(
            @Value("${spring.ai.google.genai.api-key}") String apiKey,
            @Value("${spring.ai.google.genai.model}") String model
    ) {
        HttpOptions httpOptions = HttpOptions.builder()
                .baseUrl(BASE_URL)
                .build();

        Client client = Client.builder()
                .apiKey(apiKey)
                .httpOptions(httpOptions)
                .build();

        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model(model)
                .temperature(0.7)
                .build();
        return GoogleGenAiChatModel
                .builder()
                .genAiClient(client)
                .defaultOptions(options)
                .build();
    }
}
