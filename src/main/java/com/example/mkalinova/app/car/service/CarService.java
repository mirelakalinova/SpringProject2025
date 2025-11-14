package com.example.mkalinova.app.car.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.dto.CarRepairDto;
import com.example.mkalinova.app.car.data.dto.EditCarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface CarService {

    List<AddCarDto> allCarsWithoutUser();
    <T> List<T> allCarsWithoutUser(Class<T> clazz);
    HashMap<String, String> addCarAndReturnMessage(@Valid AddCarDto addCarDto) throws AccessDeniedException;
    Optional<Car> findCar(String registrationNumber);
    boolean findByRegistrationNumber(String registrationNumber);
    boolean findByVin(String vin);
    String deleteCar(Car car);
    List<CarDto> getAll();
    List<CarRepairDto> getAllCars();
    HashMap<String, String> deleteCarById(Long id) throws AccessDeniedException;
    <T> Object getById(Long id, Class<T> clazz);
    HashMap<String, String> editCar(Long id, EditCarDto editCarDto);
    <T> Object findById(Long carId, Class<T> clazz);

    List<Car> getAllCarByClientId(Long id);
}
