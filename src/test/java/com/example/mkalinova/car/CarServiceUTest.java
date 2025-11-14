package com.example.mkalinova.car;

import com.example.mkalinova.app.apiService.service.ApiService;
import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarRepairDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarServiceImpl;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceUTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApiService apiService;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    // SUT - инжектира mock-овете горе
    @InjectMocks
    private CarServiceImpl service;

    private Car carFirst;
    private Car carSecond;
    private AddCarDto addCarDto;

    private User admin;


    @BeforeEach
    void setUp() {
        carFirst = new Car();
        carFirst.setCube(1200);
        carFirst.setHp(114);
        carFirst.setMake("Audi");
        carFirst.setModel("A4");
        carFirst.setRegistrationNumber("CB1440KH");
        carFirst.setVin("HK12KO90QW23");
        carFirst.setYear(2014);

        carSecond = new Car();
        carSecond.setCube(1300);
        carSecond.setHp(136);
        carSecond.setMake("Audi");
        carSecond.setModel("A3");
        carSecond.setRegistrationNumber("CB1540KH");
        carSecond.setVin("HK12KO901223");
        carSecond.setYear(2020);

        addCarDto = new AddCarDto();
        addCarDto.setCube(1300);
        addCarDto.setHp(136);
        addCarDto.setMake("Audi");
        addCarDto.setModel("A3");
        addCarDto.setRegistrationNumber("CB1540KH");
        addCarDto.setVin("HK12KO901223");
        addCarDto.setYear(2020);
        userRepository.deleteAll();
        admin = new User();
        admin.setFirstName("Mirela");
        admin.setLastName("Kalinova");
        admin.setUsername("admin");
        admin.setEmail("admin@test.bg");
        admin.setPassword("Password1234!");
        admin.setRole(UsersRole.ADMIN);

        userRepository.save(admin);


    }

    @Test
    void getAllCarsWithoutUser() {

        List<Car> cars = new ArrayList<>();
        cars.add(carFirst);
        cars.add(carSecond);

        when(carRepository.findByClientIsNull()).thenReturn(cars);


        when(modelMapper.map(any(Car.class), eq(AddCarDto.class)))
                .thenAnswer(invocation -> {
                    Car c = invocation.getArgument(0);
                    AddCarDto dto = new AddCarDto();
                    dto.setMake(c.getMake());
                    dto.setModel(c.getModel());
                    return dto;
                });

        List<AddCarDto> result = service.allCarsWithoutUser();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A4", result.get(0).getModel());
        assertEquals("Audi", result.get(0).getMake());
        assertEquals("Audi", result.get(1).getMake());
        assertEquals(null, result.get(0).getClient());
        assertEquals(null, result.get(1).getClient());
        verify(carRepository, times(1)).findByClientIsNull();
        verify(modelMapper, times(2)).map(any(Car.class), eq(AddCarDto.class));
    }

    @Test
    void addCarAndReturnMessage_Success() throws AccessDeniedException {
        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(carRepository.getByRegistrationNumber(addCarDto.getRegistrationNumber())).thenReturn(Optional.empty());
        when(modelMapper.map(addCarDto, Car.class))
                .thenReturn(carFirst);
        HashMap<String, String> result = service.addCarAndReturnMessage(addCarDto);
        verify(carRepository, times(1)).save(any());
        assertEquals("success", result.get("status"));
        assertEquals("Успешно добавен автомобил с рег. номер: " + addCarDto.getRegistrationNumber(), result.get("message"));


    }

    @Test
    void getAllCar_ReturnList()  {
        List<Car> cars = Arrays.asList(carFirst,carSecond);
        when(carRepository.findAll()).thenReturn(cars);
        when(modelMapper.map(any(Car.class), eq(CarRepairDto.class)))
                .thenAnswer(invocation -> {
                    Car c = invocation.getArgument(0);
                    CarRepairDto dto = new CarRepairDto();
                    dto.setMake(c.getMake());
                    dto.setModel(c.getModel());
                    return dto;
                });
        List<CarRepairDto> result = service.getAllCars();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Audi", result.get(1).getMake());



    }
    @Test
    void addCarAndReturnMessage_Error() throws AccessDeniedException {
        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(carRepository.getByRegistrationNumber(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(carFirst));
        Map<String, String> result = service.addCarAndReturnMessage(addCarDto);
        verify(modelMapper, times(0)).map(addCarDto, Car.class);
        verify(carRepository, times(0)).save(any());
        assertEquals("error", result.get("status"));
        assertEquals("Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува", result.get("message"));


    }




    @Test
    void findCar_Success(){
        when(carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber())).thenReturn(Optional.of(carFirst));
        Optional<Car> car = service.findCar(carFirst.getRegistrationNumber());
        assertNotNull(car);
        assertEquals(carFirst.getRegistrationNumber(), car.get().getRegistrationNumber());
        verify(carRepository, times(1)).findByRegistrationNumber(carFirst.getRegistrationNumber());

    }

    @Test
    void findByVin_ReturnTrue(){
        when(carRepository.findByVin(carFirst.getVin()))
                .thenReturn(Optional.of(carFirst));
        boolean car = service.findByVin(carFirst.getVin());
        assertTrue(car);
        verify(carRepository, times(1)).findByVin(carFirst.getVin());

    }

    @Test
    void findByVin_ReturnFalse(){
        when(carRepository.findByVin(carFirst.getVin()))
                .thenReturn(Optional.empty());
        boolean car = service.findByVin(carFirst.getVin());
        assertFalse(car);
        verify(carRepository, times(1)).findByVin(carFirst.getVin());

    }
    //  Changed method... may be is not neccessary to throw..
    //    @Test
    //    void findCar_Throw(){
    //        when(carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber())).thenReturn(Optional.empty());
    //        NullPointerException exception = assertThrows(
    //                NullPointerException.class,
    //                () -> service.findCar(carFirst.getRegistrationNumber()));
    //        assertTrue(exception.getMessage().contains("Кола с подадения регистрационен номер не е налична"));
    //        verify(carRepository, times(1)).findByRegistrationNumber(carFirst.getRegistrationNumber());
    //
    //    }


    @Test
    void findByRegistrationNumber_True(){
        when(carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber())).thenReturn(Optional.of(carFirst));
        boolean result = service.findByRegistrationNumber(carFirst.getRegistrationNumber());
        assertTrue(result);
        verify(carRepository, times(1))
                .findByRegistrationNumber(carFirst.getRegistrationNumber());

    }

    @Test
    void findByRegistrationNumber_False(){
        when(carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber())).thenReturn(Optional.empty());
        boolean result = service.findByRegistrationNumber(carFirst.getRegistrationNumber());
        assertFalse(result);
        verify(carRepository, times(1))
                .findByRegistrationNumber(carFirst.getRegistrationNumber());

    }
}