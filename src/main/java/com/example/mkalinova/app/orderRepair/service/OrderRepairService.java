package com.example.mkalinova.app.orderRepair.service;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;

import java.util.List;

public interface OrderRepairService {
    void saveOrderRepair(AddOrderRepairDto addOrderRepairDto, Order order);

    List<OrderRepair> findAllByOrderId(Long id);
}
