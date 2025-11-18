package com.example.mkalinova.app.carService.service;

import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.carService.data.dto.CarServiceDto;
import com.example.mkalinova.app.carService.data.dto.CarServiceListDto;
import com.example.mkalinova.app.carService.data.dto.EditCarServiceDto;


import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;

public interface CarServiceService {

     List<CarServiceListDto> getAllServicesByDeletedAtNull();
     HashMap<String, String> addService(CarServiceDto dto) throws AccessDeniedException;
     HashMap<String, String> deleteService(Long id) throws AccessDeniedException;
     HashMap<String, String> editService(EditCarServiceDto partDto) throws AccessDeniedException;

    EditCarServiceDto findById(Long id);
}
