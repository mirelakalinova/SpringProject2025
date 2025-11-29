package com.example.mkalinova.app.repair.data.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services")
public class Repair {
	@Id
	@GeneratedValue
	private UUID id;
	
	@Column
	private String name;
	
	@Column
	private double price;
	private LocalDateTime deletedAt;
	
	public Repair() {
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
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
	
	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}
}