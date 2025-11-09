package com.example.mkalinova.app.car.repo;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.carParts.data.entity.CarPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> getByRegistrationNumber(String registrationNumber);
    List<Car> findByClientIsNull();

    Optional<Car> findByRegistrationNumber(String name);

    Optional<Car> findByVin(String vin);
}
