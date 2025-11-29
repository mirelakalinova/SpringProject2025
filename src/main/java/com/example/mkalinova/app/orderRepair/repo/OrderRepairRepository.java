package com.example.mkalinova.app.orderRepair.repo;

import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepairRepository extends JpaRepository<OrderRepair, UUID> {
	List<OrderRepair> findAllByOrderId(UUID id);
	
}
