package com.example.mkalinova.app.car.service;

import com.example.mkalinova.app.car.data.dto.*;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.dto.FetchClientDto;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarService {

    List<AddCarDto> allCarsWithoutUser();

    <T> List<T> allCarsWithoutUser(Class<T> clazz);

    HashMap<String, String> addCarAndReturnMessage(@Valid AddCarDto addCarDto) throws AccessDeniedException;

    Optional<Car> findCar(String registrationNumber);

    boolean findByRegistrationNumber(String registrationNumber);

    boolean findByVin(String vin);

    String deleteCar(Car car);

    <T> List<T> getAll(Class<T> clazz);

    HashMap<String, String> deleteCarById(UUID id) throws AccessDeniedException;

    <T> Object getById(UUID id, Class<T> clazz);

    HashMap<String, String> editCar(UUID id, EditCarDto editCarDto);

    <T> Object findById(UUID carId, Class<T> clazz);

    List<Car> getAllCarByClientId(UUID id);

    List<CarListDto> fetchAllCarsByDeletedAtNull();

    List<FetchClientDto> fetchClientByCarId(UUID id);
}
