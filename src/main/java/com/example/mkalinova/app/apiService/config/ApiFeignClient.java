package com.example.mkalinova.app.apiService.config;

import com.example.mkalinova.app.apiService.data.dto.MakeListDto;
import com.example.mkalinova.app.apiService.data.dto.ModelDtoList;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@FeignClient(name = "apiClient", url = "${api.base}"
)
public interface ApiFeignClient {
	@PostMapping(value = "/api/save", consumes = "application/json")
	ResponseEntity<HashMap<String, String>> saveMakeAndModel(@RequestBody SaveMakeModelDto saveMakeModelDto);
	
	@GetMapping(value = "/api/makes", produces = "application/json")
	MakeListDto getAllMakes();
	
	@GetMapping(value = "/api/models/all", produces = "application/json")
	ModelDtoList getAllModelsWithMakes();
	
	@PostMapping(value = "/api/delete/model/{uuid}")
	ResponseEntity<HashMap<String, String>> deleteModel(@PathVariable UUID uuid);
	@PostMapping(value = "/api/delete/make/{uuid}")
	ResponseEntity<HashMap<String, String>> deleteMake(@PathVariable UUID uuid);
	
}
