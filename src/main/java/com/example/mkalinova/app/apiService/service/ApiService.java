package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.data.dto.MakeListDto;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;

public interface ApiService {

    String saveMakeAndModel(SaveMakeModelDto saveMakeModelDto);
    MakeListDto allMakes();
}
