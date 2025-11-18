package com.example.mkalinova.app.carService.repo;

import com.example.mkalinova.app.carService.data.entity.CarService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarServiceRepository extends JpaRepository<CarService, Long> {
    List<CarService> findAllByDeletedAtNull();

    Optional<CarService> findByName(String name);
}
