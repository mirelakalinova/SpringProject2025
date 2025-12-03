package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;

import java.util.HashMap;

public interface ApiService {
	
	HashMap<String, String> saveMakeAndModel(SaveMakeModelDto saveMakeModelDto);
	
}
