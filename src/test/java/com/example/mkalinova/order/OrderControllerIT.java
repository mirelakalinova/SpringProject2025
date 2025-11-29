package com.example.mkalinova.order;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.order.repo.OrderRepository;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.repo.OrderPartRepository;
import com.example.mkalinova.app.orderPart.service.OrderPartServiceImpl;
import com.example.mkalinova.app.orderRepair.service.OrderRepairServiceImpl;
import com.example.mkalinova.app.parts.service.PartService;
import com.example.mkalinova.app.repair.service.RepairService;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderControllerIT {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private CarService carService;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private PartService partService;
	@Autowired
	private RepairService repairService;
	@Autowired
	private OrderPartRepository orderPartRepository;
	
	@Autowired
	private OrderPartServiceImpl orderPartService;
	
	@Autowired
	private OrderRepairServiceImpl orderRepairService;
	
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository repository;
	private Car car;
	private OrderPart part;
	private Company company;
	private Client client;
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
		car = new Car();
		car.setVin("12fd12fd45fd12fd4");
		car.setRegistrationNumber("CB2116KH");
		car.setYear(2024);
		car.setMake("AUDI");
		car.setModel("A4");
		car.setCube(1300);
		car.setHp(120);
		car.setKw(400);
		
		carRepository.save(car);
		
		client = new Client();
		client.setPhone("0896619433");
		client.setLastName("Test");
		client.setFirstName("Test");
		clientRepository.save(client);
		
		company = new Company();
		company.setName("test");
		company.setUic("201478523");
		company.setVatNumber("BG201478523");
		company.setAddress("Test address");
		company.setAccountablePerson("Test Test");
		companyRepository.save(company);
		
	}
	
	@Test
	void AddOrderView() throws Exception {
		
		mockMvc.perform(get("/order/add")).andExpect(status().isOk()).andExpect(model().attributeExists("parts")).andExpect(model().attributeExists("repairs")).andExpect(model().attributeExists("orderDto"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void AddNewOrderWithNonValidFields_Exception() throws Exception {
		mockMvc.perform(post("/order/add").param("subtotal", "").param("tax", "")).andExpect(status().is4xxClientError());
	}
	
	
	@Test
	@WithAnonymousUser
	void AddNewOrderWithNonValidFields_AccessDenied() throws Exception {
		mockMvc.perform(post("/order/add").param("client", "").param("company", "")).andExpect(status().isForbidden());
		
	}
	
	
}