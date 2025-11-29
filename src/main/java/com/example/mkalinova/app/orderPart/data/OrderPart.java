package com.example.mkalinova.app.orderPart.data;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.parts.data.entity.Part;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders_parts")
public class OrderPart {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
	
	private String name;
	
	private int quantity;
	private double price;
	private double total;
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	@ManyToOne
	@JoinColumn(name = "part_id")
	private Part part;
	
	public OrderPart() {
	}
	
	public Part getPart() {
		return part;
	}
	
	public void setPart(Part part) {
		this.part = part;
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public Order getOrder() {
		return order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
	
	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
	
	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}
}
