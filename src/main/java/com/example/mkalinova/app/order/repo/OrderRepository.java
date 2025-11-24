package com.example.mkalinova.app.order.repo;

import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByDeletedAtNull();

//    @Query("SELECT DISTINCT о FROM Order о LEFT JOIN FETCH о.partList LEFT JOIN FETCH о.repairList WHERE о.id = :id ")
    Order findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
