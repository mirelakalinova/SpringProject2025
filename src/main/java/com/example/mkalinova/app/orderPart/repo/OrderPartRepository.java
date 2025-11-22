package com.example.mkalinova.app.orderPart.repo;

import com.example.mkalinova.app.orderPart.data.OrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPartRepository extends JpaRepository<OrderPart, Long> {
    List<OrderPart> findAllByOrderId(Long id);

}
