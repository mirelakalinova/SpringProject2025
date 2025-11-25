package com.example.mkalinova.app.car.service;

import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import com.example.mkalinova.app.apiService.service.ApiService;
import com.example.mkalinova.app.car.data.dto.*;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.client.data.dto.FetchClientDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ApiService apiService;
    private final UserServiceImpl userService;


    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, ApiService apiService, UserServiceImpl userService) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.apiService = apiService;
        this.userService = userService;
    }

    @Override
    public List<AddCarDto> allCarsWithoutUser() {
        List<AddCarDto> list = new ArrayList<>();

        carRepository.findByClientIsNull().forEach(car -> {
            AddCarDto carToAdd = modelMapper.map(car, AddCarDto.class);
            carToAdd.setModel(car.getModel());
            carToAdd.setMake(car.getMake());
            list.add(carToAdd);
        });

        return list;
    }

    @Override
    public <T> List<T> allCarsWithoutUser(Class<T> clazz) {
        List<T> list = new ArrayList<>();

        carRepository.findByClientIsNull().forEach(car -> {
            T carToAdd = modelMapper.map(car, clazz);
            list.add(carToAdd);
        });

        return list;
    }

    @Override
    public HashMap<String, String> addCarAndReturnMessage(AddCarDto addCarDto) throws AccessDeniedException {
        Optional<User> user = userService.getLoggedInUser();
        if (user.isEmpty()) {
            throw new AccessDeniedException("Нямате права да изпълните тази операция!");
        }
        HashMap<String, String> result = new HashMap<>();
        if (carRepository.getByRegistrationNumber(addCarDto.getRegistrationNumber()).isPresent()) {
            result.put("status", "error");
            result.put("message", "Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува");
            return result;
        }

        Car car = modelMapper.map(addCarDto, Car.class);
        car.setVin(car.getVin().toUpperCase());
        apiService.saveMakeAndModel(new SaveMakeModelDto(addCarDto.getMake(), addCarDto.getModel()));
        carRepository.save(car);
        result.put("status", "success");
        result.put("message", "Успешно добавен автомобил с рег. номер: " + addCarDto.getRegistrationNumber());
        return result;
    }

    @Override
    public Optional<Car> findCar(String registrationNumber) {


        return carRepository.findByRegistrationNumber(registrationNumber);
    }

    @Override
    public boolean findByRegistrationNumber(String registrationNumber) {
        return carRepository.findByRegistrationNumber(registrationNumber).isPresent();
    }

    @Override
    public boolean findByVin(String vin) {
        return carRepository.findByVin(vin).isPresent();
    }

    @Override
    public String deleteCar(Car car) {
        Optional<Car> carToDelete = carRepository.findById(car.getId());
        StringBuilder sb = new StringBuilder();
        if (carToDelete.isPresent()) {
            sb.append(carToDelete.get().getRegistrationNumber());
            carRepository.deleteById(carToDelete.get().getId());
            return sb.toString();
        } else {
            return "Няма намерена кола с това Id!";
        }
    }

    @Override
    public List<CarDto> getAll() {
        List<Car> cars = carRepository.findAllByDeletedAtNull();

        List<CarDto> carList = new ArrayList<>();

        for (Car car : cars) {
            CarDto carDto = modelMapper.map(car, CarDto.class);
            carList.add(carDto);
        }

        return carList;
    }

    @Override
    public List<CarRepairDto> getAllCars() {
        List<Car> cars = carRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Car::getRegistrationNumber)).toList();
        List<CarRepairDto> carListDto = new ArrayList<>();
        for (Car car : cars) {
            CarRepairDto carDto = modelMapper.map(car, CarRepairDto.class);
            carDto.setMake(car.getMake());
            carDto.setModel(car.getModel());
            carListDto.add(carDto);
        }

        return carListDto;
    }


    @Override
    public HashMap<String, String> deleteCarById(UUID id) throws AccessDeniedException {
        Optional<User> loggedInUser = userService.getLoggedInUser();
        if (loggedInUser.isEmpty() || !userService.isAdmin(loggedInUser.get())) {
            throw new AccessDeniedException("Нямата права да изтриете автомобил!");
        } else {
            HashMap<String, String> result = new HashMap<>();
            Optional<Car> carToDelete = carRepository.findById(id);
            if (carToDelete.isPresent()) {
                String registrationNumber = carToDelete.get().getRegistrationNumber();

                carRepository.deleteById(id);
                result.put("status", "success");
                result.put("message", "Успешно изтрита кола с рег.#: " + registrationNumber);
                return result;
            } else {

                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Няма намерен автомобил с #" + id);

            }
        }
    }

    @Override
    public <T> Object getById(UUID id, Class<T> clazz) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isPresent()) {
            return modelMapper.map(car.get(), clazz);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Автомобил #" + id + " не съществува!");
        }
    }

    @Override
    public HashMap<String, String> editCar(UUID id, EditCarDto editCarDto) {
        Optional<Car> car = carRepository.findById(id);
        HashMap<String, String> result = new HashMap<>();
        if (car.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Автомобил с #" + id + "и рег.# " + editCarDto.getRegistrationNumber() + " не съществува!");
        }

        Car carToUpdate = modelMapper.map(editCarDto, Car.class);
        carToUpdate.setId(car.get().getId());
        if (!car.get().getMake().equals(editCarDto.getMake()) && !car.get().getModel().equals(editCarDto.getModel())) {
            apiService.saveMakeAndModel(new SaveMakeModelDto(carToUpdate.getMake(), carToUpdate.getModel()));

        }
        carRepository.saveAndFlush(carToUpdate);
        String message = "Успешно обновен автомобил: " + carToUpdate.getRegistrationNumber();
        result.put("status", "success");
        result.put("message", message);

        return result;
    }

    @Override
    public <T> Object findById(UUID carId, Class<T> clazz) {
        Optional<Car> car = carRepository.findById(carId);
        // todo - if is necessary to throw NullPointer exception
        return car.<Object>map(value -> modelMapper.map(value, clazz)).orElse(null);


    }

    @Override
    public List<Car> getAllCarByClientId(UUID id) {
        return carRepository.findAllByClientId(id);

    }

    @Override
    public List<CarListDto> fetchAllCarsByDeletedAtNull() {
        List<Car> carList = carRepository.getAllByDeletedAtNull();
        List<CarListDto> listDto = new ArrayList<>();
        carList.forEach(car -> listDto.add(modelMapper.map(car, CarListDto.class)));


        return listDto;
    }

    @Override
    public List<FetchClientDto> fetchClientByCarId(UUID id) {
        Optional<Car> car = carRepository.findById(id);
        if(car.isEmpty()) {
            return null;
        }
        if(car.get().getClient() == null){
            return null;
        }

        List<FetchClientDto> list = new ArrayList<>();
        list.add(modelMapper.map(car.get().getClient(), FetchClientDto.class));
        return list;
    }


}
