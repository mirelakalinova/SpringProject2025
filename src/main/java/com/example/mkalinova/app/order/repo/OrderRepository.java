package com.example.mkalinova.app.order.repo;

import com.example.mkalinova.app.order.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByDeletedAtNull();
}
