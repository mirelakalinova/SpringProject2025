package com.example.mkalinova.app.car.service;

import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import com.example.mkalinova.app.apiService.service.ApiService;
import com.example.mkalinova.app.car.data.dto.*;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.client.data.dto.FetchClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private static final Logger log = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ApiService apiService;
    private final UserServiceImpl userService;
    private final CacheManager cacheManager;
    private final ClientRepository clientRepository;

    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, ApiService apiService, UserServiceImpl userService, CacheManager cacheManager, ClientRepository clientRepository) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.apiService = apiService;
        this.userService = userService;
        this.cacheManager = cacheManager;
        this.clientRepository = clientRepository;
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
    @Transactional
    public HashMap<String, String> addCarAndReturnMessage(AddCarDto addCarDto) throws AccessDeniedException {
        log.debug("Attempt to create car with registration number {}", addCarDto.getRegistrationNumber());
        Optional<User> user = userService.getLoggedInUser();
        if (user.isEmpty()) {
            throw new AccessDeniedException("Нямате права да изпълните тази операция!");
        }
        HashMap<String, String> result = new HashMap<>();
        if (carRepository.getByRegistrationNumber(addCarDto.getRegistrationNumber()).isPresent()) {
            result.put("status", "error");
            result.put("message", "Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува");
            log.warn("Return error message: Car with registration number {} already exists", addCarDto.getRegistrationNumber());

            return result;
        }

        Car car = modelMapper.map(addCarDto, Car.class);
        if (addCarDto.getClientId() != null) {
            Optional<Client> client = clientRepository.findById(addCarDto.getClientId());
            if (client.isPresent()) {
                car.setClient(client.get());

            } else {
                result.put("status", "error");
                result.put("message", "Нещо се обърка при добавяне на кола и закаченето ѝ към клиент! " + addCarDto.getRegistrationNumber());
                log.error("Unsuccessfully add new car with registration number: {}. The client with id {} does not exist!", addCarDto.getRegistrationNumber(), addCarDto.getClientId());
                return result;
            }

        }
        car.setVin(car.getVin().toUpperCase());
        apiService.saveMakeAndModel(new SaveMakeModelDto(addCarDto.getMake(), addCarDto.getModel()));
        carRepository.save(car);
        result.put("status", "success");
        result.put("message", "Успешно добавен автомобил с рег. номер: " + addCarDto.getRegistrationNumber());
        log.info("Created car with registration number " + addCarDto.getRegistrationNumber());
        return result;
    }


    @Override
    @Cacheable(value = "findCarByRegNum", key = "#registrationNumber", unless = "#result == null")
    public Optional<Car> findCar(String registrationNumber) {

        return carRepository.findByRegistrationNumber(registrationNumber);
    }


    @Override
    public boolean findByRegistrationNumber(String registrationNumber) {
        return carRepository.findByRegistrationNumber(registrationNumber).isPresent();
    }

    @Override
    @Cacheable(value = "findCarByVin", key = "#vin", unless = "#result == null")
    public boolean findByVin(String vin) {
        return carRepository.findByVin(vin).isPresent();
    }

    @Override
    public String deleteCar(Car car) {
        log.debug("Attempt to create car...");
        Optional<Car> carToDelete = carRepository.findById(car.getId());
        StringBuilder sb = new StringBuilder();
        if (carToDelete.isPresent()) {
            sb.append(carToDelete.get().getRegistrationNumber());
            carRepository.deleteById(carToDelete.get().getId());
            log.info("Successfully deleted car...");
            return sb.toString();
        } else {
            return "Няма намерена кола с това Id!";
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        log.debug("Attempt to get all cars...");
        List<Car> cars = carRepository.findAllByDeletedAtNull().stream()
                .sorted(Comparator.comparing(Car::getRegistrationNumber)).toList();

        List<T> carList = new ArrayList<>();

        for (Car car : cars) {
            T carDto = modelMapper.map(car, clazz);
            carList.add(carDto);
        }
        log.info("Successfully get all cars...");
        return carList;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "car", key = "#id"),
            @CacheEvict(value = "clientByCar", key = "#id")
    })
    public HashMap<String, String> deleteCarById(UUID id) throws AccessDeniedException {
        log.debug("Attempt to delete a car with id {}", id);
        Optional<User> loggedInUser = userService.getLoggedInUser();
        if (loggedInUser.isEmpty() || !userService.isAdmin(loggedInUser.get())) {
            throw new AccessDeniedException("Нямата права да изтриете автомобил!");
        } else {
            HashMap<String, String> result = new HashMap<>();
            Optional<Car> carToDelete = carRepository.findById(id);
            if (carToDelete.isPresent()) {
                String registrationNumber = carToDelete.get().getRegistrationNumber();
                if (cacheManager.getCache("findCarByRegNum") != null) {
                    cacheManager.getCache("findCarByRegNum").evict(carToDelete.get().getRegistrationNumber());
                }
                if (cacheManager.getCache("findCarByVin") != null) {
                    cacheManager.getCache("findCarByVin").evict(carToDelete.get().getVin());
                }

                if (cacheManager.getCache("car") != null) {
                    cacheManager.getCache("car").evict(id);
                }
                carToDelete.get().setDeletedAt(LocalDateTime.now());
                carRepository.save(carToDelete.get());
                result.put("status", "success");
                result.put("message", "Успешно изтрита кола с рег.#: " + registrationNumber);
                log.info("Successfully deleted car with id {}", id);
                return result;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма намерен автомобил с #" + id);

            }
        }
    }

    @Override
    @Cacheable(value = "car", key = "#id", unless = "#result == null")
    public <T> Object getById(UUID id, Class<T> clazz) {
        log.debug("Attempt to get a car with id {}", id);
        Optional<Car> car = carRepository.findById(id);
        if (car.isPresent()) {
            log.info("Successfully get car with id {}", id);

            return modelMapper.map(car.get(), clazz);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Автомобил #" + id + " не съществува!");
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "findCarByRegNum", key = "#editCarDto.registrationNumber"),
            @CacheEvict(value = "findCarByVin", key = "#editCarDto.vin"),
            @CacheEvict(value = "car", key = "#id"),
            @CacheEvict(value = "clientByCar", key = "#id")
    })
    public HashMap<String, String> editCar(UUID id, EditCarDto editCarDto) {
        log.debug("Attempt to edit a car with id {}", id);
        Optional<Car> car = carRepository.findById(id);
        HashMap<String, String> result = new HashMap<>();
        if (car.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Автомобил с #" + id + "и рег.# " + editCarDto.getRegistrationNumber() + " не съществува!");
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
        log.info("Successfully edit car with id {}", id);
        return result;
    }

    @Override
    @Cacheable(value = "car", key = "#carId", unless = "#result == null")
    public <T> Object findById(UUID carId, Class<T> clazz) {
        log.debug("Attempt to find car by id {}", carId);
        Optional<Car> car = carRepository.findById(carId);
        return car.<Object>map(value -> modelMapper.map(value, clazz)).orElse(null);


    }

    @Override
    public List<Car> getAllCarByClientId(UUID id) {
        log.debug("Attempt to get all cars by client with id {}", id);
        return carRepository.findAllByClientId(id);

    }

    @Override
    public List<CarListDto> fetchAllCarsByDeletedAtNull() {
        List<Car> carList = carRepository.getAllByDeletedAtNull();
        List<CarListDto> listDto = new ArrayList<>();
        carList.forEach(car -> listDto.add(modelMapper.map(car, CarListDto.class)));


        log.debug("Attempt to fetch all cars by deleted field is null");
        return listDto;
    }

    @Override
    @Cacheable(value = "clientByCar", key = "#id", unless = "#result == null")
    public List<FetchClientDto> fetchClientByCarId(UUID id) {
        Optional<Car> car = carRepository.findById(id);
        log.debug("Attempt to fetch client by car id {}", id);
        if (car.isEmpty() || car.get().getClient() == null) {
            return null;
        }
        List<FetchClientDto> list = new ArrayList<>();
        list.add(modelMapper.map(car.get().getClient(), FetchClientDto.class));
        log.info("Successfully fetch client by car id {}", id);
        return list;
    }


}
