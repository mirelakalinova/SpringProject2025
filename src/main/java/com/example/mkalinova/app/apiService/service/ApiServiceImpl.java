package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.config.ApiFeignClient;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ApiServiceImpl implements ApiService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);  // Статично инициализиране на логгера
	private final ApiFeignClient apiFeignClient;
	
	public ApiServiceImpl(ApiFeignClient apiFeignClient) {
		this.apiFeignClient = apiFeignClient;
	}
	
	@Override
	public HashMap<String, String> saveMakeAndModel(SaveMakeModelDto saveMakeModelDto) {
		ResponseEntity<HashMap<String,String>> resp = apiFeignClient.saveMakeAndModel(saveMakeModelDto);
		System.out.println(resp.getHeaders());
		System.out.println(resp.getBody());
		return resp.getBody();
		
	}
	
}
