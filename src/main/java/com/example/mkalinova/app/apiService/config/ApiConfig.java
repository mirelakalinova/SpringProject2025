package com.example.mkalinova.app.apiService.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-vehicle")
public class ApiConfig {

    private String base;

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
