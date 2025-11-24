package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.data.entity.Order;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;


public interface OrderService {

    HashMap<String, String> saveOrder(AddOrderDto orderDto) throws AccessDeniedException;
    HashMap<String, String> editOrder(Long id, EditOrderDto dto) throws AccessDeniedException;
    HashMap<String, String> deleteOrder(Long id);


    List<OrderListDto> getAllOrders() throws AccessDeniedException;

    EditOrderDto getOrderById(Long id);
}
