package com.fitu.fitu.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${restClient.baseUrl}")
    private String baseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(),
                        (request, response) -> {
                            if (response.getStatusCode().is4xxClientError()) {
                                throw new RuntimeException("Client Error");
                            }
                            if (response.getStatusCode().is5xxServerError()) {
                                throw new RuntimeException("Server Error");
                            }
                        })
                .build();
    }
}