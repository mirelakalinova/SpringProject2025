package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.data.entity.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


public interface OrderService {

    HashMap<String, String> saveOrder(AddOrderDto orderDto);
    HashMap<String, String> editOrder(Long id);
    HashMap<String, String> deleteOrder(Long id);


    List<OrderListDto> getAllOrders();
}
