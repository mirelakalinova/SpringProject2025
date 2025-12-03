package com.example.mkalinova.app.apiService.service;

import com.example.mkalinova.app.apiService.config.ApiFeignClient;
import com.example.mkalinova.app.apiService.data.dto.*;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class ApiServiceImpl implements ApiService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);  // Статично инициализиране на логгера
	private final ApiFeignClient apiFeignClient;
	private final ModelMapper modelMapper;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserService userService;
	
	private static final int PAGE_SIZE = 25;
	
	public ApiServiceImpl(ApiFeignClient apiFeignClient, ModelMapper modelMapper, RedisTemplate<String, Object> redisTemplate, UserService userService) {
		this.apiFeignClient = apiFeignClient;
		this.modelMapper = modelMapper;
		this.redisTemplate = redisTemplate;
		
		this.userService = userService;
	}
	
	@Override
	public HashMap<String, String> saveMakeAndModel(SaveMakeModelDto saveMakeModelDto) throws AccessDeniedException {
		userService.isUserLogIn();
		ResponseEntity<HashMap<String, String>> resp = apiFeignClient.saveMakeAndModel(saveMakeModelDto);
		evictAllModelsCache();
		return resp.getBody();
		
	}
	
	@Cacheable(value = "allModels")
	public List<ModelDto> loadAllModelsFromApi() {
		ModelDtoList rawModelList = apiFeignClient.getAllModelsWithMakes();
		if (rawModelList == null || rawModelList.getModels() == null) {
			return Collections.emptyList();
		}
		return rawModelList.getModels();
	}
	
	@Override
	public Page<ModelDto> getModelsPage(Pageable pageable) {
		int page = pageable.getPageNumber();
		
		
		List<ModelDto> rawModelList = loadAllModelsFromApi();
		int total = rawModelList.size();
		
		int fromIndex = Math.min(page * PAGE_SIZE, total);
		int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
		
		List<ModelDto> content = rawModelList.subList(fromIndex, toIndex);
		
		return new PageImpl<>(content, PageRequest.of(page, PAGE_SIZE, pageable.getSort()), total);
	}
	
	@Override
	public HashMap<String, String> deleteModel(UUID uuid) throws AccessDeniedException {
		Optional<User> user = userService.getLoggedInUser();
		if (user.isEmpty()) {
			userService.isUserLogIn();
		} else {
			userService.isAdmin(user.get());
			
		}
		return apiFeignClient.deleteModel(uuid).getBody();
	}
	
	@CacheEvict(value = "allModels", allEntries = true)
	public void evictAllModelsCache() {
	}
	
	
	@Cacheable(value = "allMakes")
	public List<MakeDto> loadAllMakes() {
		MakeListDto rawModelList = apiFeignClient.getAllMakes();
		if (rawModelList == null || rawModelList.getMakes() == null) {
			return Collections.emptyList();
		}
		return rawModelList.getMakes();
	}
	
	@Override
	public Page<MakeDto> getMakesPage(Pageable pageable) {
		int page = pageable.getPageNumber();
		
		List<MakeDto> rawModelList = loadAllMakes();
		int total = rawModelList.size();
		
		int fromIndex = Math.min(page * PAGE_SIZE, total);
		int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
		
		List<MakeDto> content = rawModelList.subList(fromIndex, toIndex);
		
		return new PageImpl<>(content, PageRequest.of(page, PAGE_SIZE, pageable.getSort()), total);
	}
	
	@Override
	public HashMap<String, String> deleteMake(UUID uuid) throws AccessDeniedException {
		Optional<User> user = userService.getLoggedInUser();
		if (user.isEmpty()) {
			userService.isUserLogIn();
		} else {
			userService.isAdmin(user.get());
			
		}
		return apiFeignClient.deleteMake(uuid).getBody();
	}
	
	@CacheEvict(value = "allMakes", allEntries = true)
	public void evictAllMakesCache() {
	}
	
}
