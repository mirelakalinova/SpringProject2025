package com.example.mkalinova.app.orderPart.service;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;

import java.util.List;

public interface OrderPartService {

    void saveOrderPart(AddOrderPartDto addOrderPartDto, Order order);

    List<OrderPart> findAllByOrderId(Long id);

    void setDeletedAtAllByOrderId(Long id);
    void deletedAllByOrderId(Long id);
}
