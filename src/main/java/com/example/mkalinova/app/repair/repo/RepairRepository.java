package com.example.mkalinova.app.repair.repo;


import com.example.mkalinova.app.repair.data.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepairRepository extends JpaRepository<Repair, UUID> {
    List<Repair> findAllByDeletedAtNull();

    Optional<Repair> findByName(String name);
}
