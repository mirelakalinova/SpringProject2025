package com.example.mkalinova.app.apiService.data.dto;

import java.util.UUID;

public class ModelDto {

	
	private UUID id;
	private String name;
	private String make;
	
	public ModelDto() {
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMake() {
		return make;
	}
	
	public void setMake(String make) {
		this.make = make;
	}
}
