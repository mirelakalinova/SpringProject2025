package com.example.mkalinova.app.apiService.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SaveMakeModelDto {
	@NotBlank
	@NotNull
	@Size(min = 3, message = "Марката може да е най-малко 3 символа!")
	private String makeName;
	@NotBlank
	@NotNull
	@Size(min = 3, message = "Моделът може да е най-малко 3 символа!")
	private String modelName;
	
	public SaveMakeModelDto() {
	}
	
	public SaveMakeModelDto(String makeName, String modelName) {
		this.makeName = makeName;
		this.modelName = modelName;
	}
	
	public String getMakeName() {
		return makeName;
	}
	
	public void setMakeName(String makeName) {
		this.makeName = makeName;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
