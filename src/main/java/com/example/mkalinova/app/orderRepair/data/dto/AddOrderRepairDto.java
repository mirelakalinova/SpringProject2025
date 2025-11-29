package com.example.mkalinova.app.orderRepair.data.dto;


import java.util.UUID;

public class AddOrderRepairDto {
	
	private UUID id;
	
	
	private int quantity;
	private String name;
	private Double price;
	private Double total;
	
	public AddOrderRepairDto() {
	
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
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
	
	public Double getTotal() {
		return total;
	}
	
	public void setTotal(Double total) {
		this.total = total;
	}
}
