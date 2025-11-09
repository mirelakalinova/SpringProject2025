package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.config.ApiConfig;
import com.example.mkalinova.app.apiService.data.dto.MakeListDto;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApiServiceImpl implements ApiService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);  // Статично инициализиране на логгера
    private final ApiConfig apiConfig;
    private final RestClient restClient;

    public ApiServiceImpl(ApiConfig apiConfig, @Qualifier("apiRestClient") RestClient restClient) {

        this.apiConfig = apiConfig;
        this.restClient = restClient;
    }

    @Override
    public String saveMakeAndModel(SaveMakeModelDto saveMakeModelDto) {
        return "";
    }

    @Override
    public MakeListDto allMakes() {
        String url = apiConfig.getBase() + "/api/makes";

        return restClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

    }
}
