package com.example.mkalinova.app.order.data.dto;

import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddOrderDto {
	
	
	@NotNull
	private double subtotal;
	@PositiveOrZero
	private double discount;
	@PositiveOrZero
	private double discountAmount;
	@PositiveOrZero
	private double discountPercent;
	@NotNull
	private double tax;
	@NotNull
	private double total;
	private String note;
	
	@NotNull
	private UUID carId;
	private UUID clientId;
	private UUID companyId;
	private List<AddOrderPartDto> parts;
	@NotNull
	@NotEmpty(message = "Моля, въведете услуга!")
	private List<AddOrderRepairDto> repairs;
	
	public AddOrderDto() {
		this.parts = new ArrayList<>();
		this.repairs = new ArrayList<>();
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
	
	public UUID getCarId() {
		return carId;
	}
	
	public void setCarId(UUID carId) {
		this.carId = carId;
	}
	
	public UUID getClientId() {
		return clientId;
	}
	
	public void setClientId(UUID clientId) {
		this.clientId = clientId;
	}
	
	public UUID getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(UUID companyId) {
		this.companyId = companyId;
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
