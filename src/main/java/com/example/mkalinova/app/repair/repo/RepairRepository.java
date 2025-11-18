package com.example.mkalinova.app.repair.repo;


import com.example.mkalinova.app.repair.data.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findAllByDeletedAtNull();

    Optional<Repair> findByName(String name);
}
