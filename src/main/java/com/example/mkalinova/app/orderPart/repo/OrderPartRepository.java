package com.example.mkalinova.app.orderPart.repo;

import com.example.mkalinova.app.orderPart.data.OrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderPartRepository extends JpaRepository<OrderPart, UUID> {
	List<OrderPart> findAllByOrderId(UUID id);
	
}
