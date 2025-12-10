package com.example.mkalinova.app.order.data.dto;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditOrderDto {
	
	private UUID id;
	@NotNull
	private double subtotal;
	@NotNull
	private double discount;
	private double discountAmount;
	private double discountPercent;
	@NotNull
	private double tax;
	@NotNull
	private double total;
	private String note;
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	@NotNull
	private Car car;
	private Client client;
	private Company company;
	private List<AddOrderPartDto> parts;
	@NotNull
	@NotEmpty
	private List<AddOrderRepairDto> repairs;
	
	public EditOrderDto() {
		this.parts = new ArrayList<>();
		this.repairs = new ArrayList<>();
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public double getSubtotal() {
		return subtotal;
	}
	
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	
	public double getDiscount() {
		return discount;
	}
	
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	
	public double getDiscountAmount() {
		return discountAmount;
	}
	
	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	public double getDiscountPercent() {
		return discountPercent;
	}
	
	public void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}
	
	public double getTax() {
		return tax;
	}
	
	public void setTax(double tax) {
		this.tax = tax;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public Car getCar() {
		return car;
	}
	
	public void setCar(Car car) {
		this.car = car;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getEditedAt() {
		return editedAt;
	}
	
	public void setEditedAt(LocalDateTime editedAt) {
		this.editedAt = editedAt;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public List<AddOrderPartDto> getParts() {
		return parts;
	}
	
	public void setParts(List<AddOrderPartDto> parts) {
		this.parts = parts;
	}
	
	public List<AddOrderRepairDto> getRepairs() {
		return repairs;
	}
	
	public void setRepairs(List<AddOrderRepairDto> repairs) {
		this.repairs = repairs;
	}
}
