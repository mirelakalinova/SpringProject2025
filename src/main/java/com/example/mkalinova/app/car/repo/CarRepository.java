package com.example.mkalinova.app.car.repo;

import com.example.mkalinova.app.car.data.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {

    Optional<Car> getByRegistrationNumber(String registrationNumber);
    List<Car> findByClientIsNull();

    Optional<Car> findByRegistrationNumber(String name);

    Optional<Car> findByVin(String vin);

    List<Car> findAllByClientId(UUID id);

    List<Car> findAllByDeletedAtNull();

    List<Car> getAllByDeletedAtNull();
}
