package com.example.mkalinova.app.apiService.config;

import com.example.mkalinova.app.apiService.data.dto.MakeListDto;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "apiClient", url = "${api.base}")
public interface ApiFeignClient {
    @PostMapping(value = "/api/save", consumes = "application/json")
    void saveMakeAndModel(@RequestBody SaveMakeModelDto saveMakeModelDto);

    @GetMapping(value = "/api/makes", produces = "application/json")
    MakeListDto getAllMakes();

}
