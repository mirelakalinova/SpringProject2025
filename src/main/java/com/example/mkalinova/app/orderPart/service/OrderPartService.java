package com.example.mkalinova.app.orderPart.service;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;

import java.util.List;
import java.util.UUID;

public interface OrderPartService {
	
	void saveOrderPart(AddOrderPartDto addOrderPartDto, Order order);
	
	List<OrderPart> findAllByOrderId(UUID id);
	
	void setDeletedAtAllByOrderId(UUID id);
	
	void deletedAllByOrderId(UUID id);
}
