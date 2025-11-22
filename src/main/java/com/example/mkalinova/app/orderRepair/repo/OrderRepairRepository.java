package com.example.mkalinova.app.orderRepair.repo;

import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepairRepository  extends JpaRepository<OrderRepair,  Long> {
    List<OrderRepair> findAllByOrderId(Long id);

}
