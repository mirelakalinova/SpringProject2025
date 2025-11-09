package com.example.mkalinova.app.apiService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {


    @Bean
    public RestClient genericRestClient() {
        return RestClient.create();
    }

    @Bean("apiRestClient")
    public RestClient apiRestClient() {
        return RestClient.builder()
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
