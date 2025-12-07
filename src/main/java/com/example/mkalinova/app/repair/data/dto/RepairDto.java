package com.example.mkalinova.app.repair.data.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class RepairDto {
	
	@NotNull
	@Size(min = 3, message = "Името трябва да е поне 3 символа")
	private String name;
	
	@PositiveOrZero
	private Double price;
	
	public RepairDto() {
	
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
}
