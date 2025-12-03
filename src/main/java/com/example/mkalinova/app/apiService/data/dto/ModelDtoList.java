package com.example.mkalinova.app.apiService.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ModelDtoList {
	
	@JsonProperty("models")
	private List<ModelDto> models;
	
	private Long totalElements;
	private Integer totalPages;
	
	public ModelDtoList() {
	}
	
	public List<ModelDto> getModels() {
		return models;
	}
	
	public void setModels(List<ModelDto> models) {
		this.models = models;
	}
	
	public Long getTotalElements() {
		return totalElements;
	}
	
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}
	
	public Integer getTotalPages() {
		return totalPages;
	}
	
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
}
