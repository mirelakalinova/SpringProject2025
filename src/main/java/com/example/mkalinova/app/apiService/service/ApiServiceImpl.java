package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.data.dto.MakeListDto;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import com.example.mkalinova.app.apiService.config.ApiFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl implements ApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);  // Статично инициализиране на логгера
    private final ApiFeignClient apiFeignClient;

    public ApiServiceImpl(
            ApiFeignClient apiFeignClient) {
        this.apiFeignClient = apiFeignClient;
    }


    @Override
    public void saveMakeAndModel(SaveMakeModelDto saveMakeModelDto) {
        apiFeignClient.saveMakeAndModel(saveMakeModelDto);

    }

    @Override
    public MakeListDto getAllMakes() {
        return apiFeignClient.getAllMakes();
    }
}
