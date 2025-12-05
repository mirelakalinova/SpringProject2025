package com.example.mkalinova.car;

import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
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
	@Autowired
	private ClientRepository clientRepository;
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
		admin.setEnabled(true);
		
		userRepository.save(admin);
		
		
		editor = new User();
		editor.setFirstName("editor");
		editor.setLastName("editor");
		editor.setUsername("editor");
		editor.setEmail("editor@test.bg");
		editor.setPassword("Password1234!");
		editor.setRole(UsersRole.EDITOR);
		editor.setEnabled(true);
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
						.param("id", String.valueOf(carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId()))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/car/cars"));
		
		
		Optional<Car> car = carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber());
		assertTrue(car.isPresent());
		assertNotNull(car.get().getDeletedAt());
		
		
	}
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void DeleteCarByEditor_ThrowAccessDenied() throws Exception {
		mockMvc.perform(post("/car/delete/{id}", carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId())
						.param("id", String.valueOf(carRepository.getByRegistrationNumber(carFirst.getRegistrationNumber()).get().getId()))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		
		Optional<Car> car = carRepository.findByRegistrationNumber(carFirst.getRegistrationNumber());
		assertTrue(car.isPresent());
		assertNull(car.get().getDeletedAt());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void DeleteCarByAdmin_ThrowResponseStatusException() throws Exception {
		String id = String.valueOf(UUID.randomUUID());
		mockMvc.perform(post("/car/delete/{id}", id)
						.param("id", String.valueOf(id))
						.with(csrf()))
				.andExpect(status().is5xxServerError());
		
		
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
		Optional<Car> carById = carRepository.findById(UUID.randomUUID());
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
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void fetchAllCars_ReturnList() throws Exception {
		carSecond.setDeletedAt(LocalDateTime.now());
		carRepository.save(carSecond);
		
		mockMvc.perform(get("/car/fetch/cars")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cars").isArray())
				.andExpect(jsonPath("$.cars.length()").value(1))
				.andExpect(jsonPath("$.cars[0].registrationNumber").value("CB2116KH"));
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void fetchClientByCarId_ReturnList() throws Exception {
		
		
		Client client = new Client();
		client.setPhone("0896619422");
		
		client.setFirstName("Test");
		client.setLastName("Test");
		client.setCars(List.of(carFirst));
		clientRepository.save(client);
		carFirst.setClient(client);
		carRepository.save(carFirst);
		mockMvc.perform(get("/car/fetch/client/{id}", carFirst.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.clients").isArray())
				.andExpect(jsonPath("$.clients.length()").value(1))
				.andExpect(jsonPath("$.clients[0].phone").value("0896619422"));
		
	}
	
}
