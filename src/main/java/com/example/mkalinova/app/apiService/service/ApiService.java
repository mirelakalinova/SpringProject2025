package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.data.dto.MakeDto;
import com.example.mkalinova.app.apiService.data.dto.ModelDto;
import com.example.mkalinova.app.apiService.data.dto.ModelDtoList;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.UUID;

public interface ApiService {
	
	HashMap<String, String> saveMakeAndModel(SaveMakeModelDto saveMakeModelDto) throws AccessDeniedException;
	
	Page<ModelDto> getModelsPage(Pageable pageable);
	Page<MakeDto> getMakesPage(Pageable pageable);
	
	HashMap<String, String> deleteModel(UUID uuid) throws AccessDeniedException;
	
	HashMap<String, String> deleteMake(UUID uuid) throws AccessDeniedException;
}
