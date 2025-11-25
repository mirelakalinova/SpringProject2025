package com.example.mkalinova.car;

import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.user.data.dto.UserListDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ModelMapper modelMapper;
    private Car carFirst;
    private Car carSecond;
    @Autowired
    private UserRepository userRepository;
    private User admin;
    private User editor;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        admin = new User();
        admin.setFirstName("Mirela");
        admin.setLastName("Kalinova");
        admin.setUsername("admin");
        admin.setEmail("admin@test.bg");
        admin.setPassword("Password1234!");
        admin.setRole(UsersRole.ADMIN);

        userRepository.save(admin);


        editor = new User();
        editor.setFirstName("editor");
        editor.setLastName("editor");
        editor.setUsername("editor");
        editor.setEmail("editor@test.bg");
        editor.setPassword("Password1234!");
        editor.setRole(UsersRole.EDITOR);
        userRepository.save(editor);
        carRepository.deleteAll();
        carFirst = new Car();
        carFirst.setVin("12fd12fd45fd12fd4");
        carFirst.setRegistrationNumber("CB2116KH");
        carFirst.setYear(2024);
        carFirst.setMake("AUDI");
        carFirst.setModel("A4");
        carFirst.setCube(1300);
        carFirst.setHp(120);
        carFirst.setKw(400);

        carRepository.save(carFirst);
        carSecond = new Car();
        carSecond.setVin("12fd12fd45fd12ff4");
        carSecond.setRegistrationNumber("CB2116KC");
        carSecond.setYear(2022);
        carSecond.setMake("BMW");
        carSecond.setModel("313i");
        carSecond.setCube(1200);
        carSecond.setHp(110);
        carSecond.setKw(200);
        carRepository.save(carSecond);
    }

    @Test
    public void getCarList() throws Exception {
        ArrayList<CarDto> list = new ArrayList<>();
        list.add(modelMapper.map(carFirst, CarDto.class));
        list.add(modelMapper.map(carSecond, CarDto.class));
        mockMvc.perform(get("/car/cars")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cars", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void DeleteCarByAdmin_Success() throws Exception {
        mockMvc.perform(post("/car/delete/{id}", carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId())
                        .param("id", String.valueOf( carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/cars"));


        Optional<Car> car = carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber());
        assertTrue(car.isEmpty());


    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void DeleteCarByEditor_ThrowAccessDenied() throws Exception {
        mockMvc.perform(post("/car/delete/{id}", carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId())
                        .param("id", String.valueOf( carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId()))
                        .with(csrf()))
                .andExpect(status().isForbidden());



        Optional<Car> car = carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber());
        assertTrue(car.isPresent());
        assertTrue(car.get().getRegistrationNumber().equals(carFirst.getRegistrationNumber()));


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void DeleteCarByAdmin_ThrowResponseStatusException() throws Exception {
        String id = String.valueOf(UUID.randomUUID());
        mockMvc.perform(post("/car/delete/{id}", id)
                        .param("id", String.valueOf(id))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());



        Optional<Car> car = carRepository.findById(UUID.randomUUID());
        assertTrue(car.isEmpty());



    }

    @Test

    public void editCarByAdmin_Success() throws Exception {
        mockMvc.perform(post("/car/edit/{id}", carFirst.getId())
                        .param("id", String.valueOf(carFirst.getId()))
                        .param("registrationNumber", "KH2000K")
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/car/cars"));

        Optional<Car> car = carRepository.findByRegistrationNumber("KH2000K");
        assertTrue(car.isPresent());
        Optional<Car> carById = carRepository.findById(carFirst.getId());
        assertTrue(carById.isPresent());
        assertEquals("KH2000K", car.get().getRegistrationNumber());

    }


    @Test
    public void editCarByLoggedInUser_Return() throws Exception {
        mockMvc.perform(post("/car/edit/{id}", carFirst.getId())
                        .param("id", String.valueOf(carFirst.getId()))
                        .param("registrationNumber", "KH2000")
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/edit/" + carFirst.getId()));

        Optional<Car> car = carRepository.findByRegistrationNumber("KH2000");
        assertTrue(car.isEmpty());


    }

   @Test
   @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addCarByLoggedInUser_Success() throws Exception {
        mockMvc.perform(post("/car/add")
                        .param("registrationNumber", "KH2000K")
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/car/cars"));

        Optional<Car> car = carRepository.findByRegistrationNumber("KH2000K");
        assertTrue(car.isPresent());
        Optional<Car> carById = carRepository.findById(carFirst.getId());
        assertTrue(carById.isPresent());
        assertEquals("KH2000K", car.get().getRegistrationNumber());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addCarWithExistingRegNumber_ReturnMessage() throws Exception {
        mockMvc.perform(post("/car/add")
                        .param("registrationNumber", carFirst.getRegistrationNumber())
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/car/add"));

        Optional<Car> car = carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber());
        assertTrue(car.isPresent());
        Optional<Car> carById = carRepository.findById(UUID.randomUUID() );
        assertFalse(carById.isPresent());


    }

    @Test
    public void addCarWithErrors_Redirect() throws Exception {
        mockMvc.perform(post("/car/add")
                        .param("registrationNumber", "KH2000")
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/add"));

        Optional<Car> car = carRepository.findByRegistrationNumber("KH2000K");
        assertTrue(car.isEmpty());
        }

    @Test
    @WithAnonymousUser
    public void addCarByAnonymous_AccessDenied() throws Exception {
        mockMvc.perform(post("/car/add")
                        .param("registrationNumber", "KH2000H")
                        .param("vin", carFirst.getVin())
                        .param("model", carFirst.getModel())
                        .param("make", carFirst.getMake())
                        .param("hp", String.valueOf(carFirst.getHp()))
                        .param("kw", String.valueOf(carFirst.getKw()))
                        .param("year", String.valueOf(carFirst.getYear()))
                        .param("cube", String.valueOf(carFirst.getCube()))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<Car> car = carRepository.findByRegistrationNumber("KH2000K");
        assertTrue(car.isEmpty());
    }


}
