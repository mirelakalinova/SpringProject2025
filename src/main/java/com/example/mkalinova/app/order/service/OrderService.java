package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public interface OrderService {
	
	HashMap<String, String> saveOrder(AddOrderDto orderDto) throws AccessDeniedException;
	
	HashMap<String, String> editOrder(UUID id, EditOrderDto dto) throws AccessDeniedException;
	
	HashMap<String, String> deleteOrder(UUID id);
	
	
	List<OrderListDto> getAllOrders() throws AccessDeniedException;
	
	EditOrderDto getOrderById(UUID id);
	int cleanOrder(LocalDateTime date);
}
