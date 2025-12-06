package com.example.mkalinova.client;

import com.example.mkalinova.app.apiService.config.ApiFeignClient;
import com.example.mkalinova.app.apiService.data.dto.SaveMakeModelDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.dto.ClientListCarDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class ClientControllerIT {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private CarService carService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private UserRepository userRepository;
	@InjectMocks
	private ModelMapper modelMapper;
	@MockitoBean
	private ApiFeignClient apiFeignClient;
	@Autowired
	private Validator validator;
	private Client clientFirst;
	private Client clientSecond;
	
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		carRepository.deleteAll();
		companyRepository.deleteAll();
		clientRepository.deleteAll();
		
		clientFirst = new Client();
		clientFirst.setPhone("0896619422");
		clientFirst.setFirstName("Test");
		clientFirst.setLastName("Test");
		clientFirst.setEmail("projects@zashev.com");
		clientRepository.saveAndFlush(clientFirst);
		
		clientSecond = new Client();
		clientSecond.setPhone("0896619423");
		clientSecond.setFirstName("Test2");
		clientSecond.setLastName("Test2");
		clientSecond.setEmail("projects2@zashev.com");
		clientRepository.saveAndFlush(clientSecond);
		
		User user = new User();
		user.setRole(UsersRole.ADMIN);
		user.setUsername("admin");
		user.setPassword("JEcame4032!");
		user.setFirstName("Тест");
		user.setLastName("Тест");
		user.setEnabled(true);
		User editor = new User();
		editor.setRole(UsersRole.EDITOR);
		editor.setUsername("editor");
		editor.setPassword("JEcame4032!");
		editor.setFirstName("Тест");
		editor.setLastName("Тест");
		editor.setEnabled(true);
		userRepository.saveAndFlush(user);
		userRepository.saveAndFlush(editor);
		
		
	}
	
	@Test
	public void getClientList() throws Exception {
		ArrayList<ClientListCarDto> list = new ArrayList<>();
		list.add(modelMapper.map(clientFirst, ClientListCarDto.class));
		clientSecond.setDeletedAt(LocalDateTime.now());
		clientRepository.saveAndFlush(clientSecond);
		list.add(modelMapper.map(clientSecond, ClientListCarDto.class));
		mockMvc.perform(get("/client/list").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk()).andExpect(model().attribute("clients", hasSize(1)));
	}
	
	@Test
	public void getAddClient() throws Exception {
		Company company = new Company();
		company.setName("test");
		company.setUic("201478523");
		company.setVatNumber("BG201478523");
		company.setAddress("Test address");
		company.setAccountablePerson("Test Test");
		
		Company companySecond = new Company();
		companySecond.setName("test2");
		companySecond.setUic("201478524");
		companySecond.setVatNumber("BG201478524");
		companySecond.setAddress("Test address");
		companySecond.setAccountablePerson("Test Test");
		companySecond.setDeleteAd(LocalDateTime.now());
		companySecond.setClient(clientFirst);
		
		companyRepository.saveAndFlush(company);
		companyRepository.saveAndFlush(companySecond);
		mockMvc.perform(get("/client/add").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk()).andExpect(model().attribute("companies", hasSize(1)));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createUser_WithValidClientDto_RedirectsToClients() throws Exception {
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819422").with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/list"));
		
		Optional<Client> client = clientRepository.findByPhone("0898819422");
		assertTrue(client.isPresent());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_WithValidClientDtoAndValidCarDto_RedirectsToClients() throws Exception {
		HashMap<String, String> apiResp = new HashMap<>();
		apiResp.put("status", "success");
		when(apiFeignClient.saveMakeAndModel(any(SaveMakeModelDto.class))).thenReturn(ResponseEntity.ok(apiResp));
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819422").param("registrationNumber", "CB2126KO").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109177")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/list"));
		Optional<Client> client = clientRepository.findByPhone("0898819422");
		assertTrue(client.isPresent());
		Optional<Car> car = carRepository.findByRegistrationNumber("CB2126KO");
		assertTrue(car.isPresent());
		
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_WithValidClientDtoAndNonValidCarDto_MessageError() throws Exception {
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819422")
						//Car
						.param("registrationNumber", "CB25K").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109177")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/add"));
		Optional<Client> client = clientRepository.findByPhone("0898819422");
		assertFalse(client.isPresent());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_WithValidClientDtoAndValidCarDtoAndCompany_Success() throws Exception {
		HashMap<String, String> apiResp = new HashMap<>();
		apiResp.put("status", "success");
		when(apiFeignClient.saveMakeAndModel(any(SaveMakeModelDto.class))).thenReturn(ResponseEntity.ok(apiResp));
		mockMvc.perform(post("/client/add").param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819455").param("registrationNumber", "CB2125KP").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109188").param("checked", String.valueOf(true)).param("name", "Audi").param("uic", "201799238").param("vatNumber", "BG201799238").param("address", "1HGBH41JXMN109188").param("accountablePerson", "Test test")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/list"));
		
		Optional<Client> client = clientRepository.findByPhone("0898819455");
		assertTrue(client.isPresent());
		Optional<Car> car = carRepository.findByRegistrationNumber("CB2125KP");
		assertTrue(car.isPresent());
		Optional<Company> company = companyRepository.findByUic("201799238");
		assertTrue(company.isPresent());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_WithValidClientDtoAndValidCarDtoAndNonValidCompany_RedirectsToClients() throws Exception {
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819422")
						//Car
						.param("registrationNumber", "CB2125KO").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109177").param("checked", String.valueOf(true)).param("name", "").param("uic", "2017996").param("vatNumber", "BG201799236").param("address", "1HGBH41JXMN109177").param("accountablePerson", "Test test")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/add"));
		Optional<Client> client = clientRepository.findByPhone("0898819422");
		assertFalse(client.isPresent());
		Optional<Car> car = carRepository.findByRegistrationNumber("CB2125KO");
		assertFalse(car.isPresent());
		
	}
	
	@Test
	@WithAnonymousUser
	public void createClient_WithValidClientDto_AccessDenied() throws Exception {
		
		mockMvc.perform(post("/client/add")
				
				.param("firstName", "Test").param("lastName", "Testov").param("email", "projects23@gmail.com").param("phone", "0898819422").with(csrf())).andExpect(status().isForbidden());
		Optional<Client> client = clientRepository.findByPhone("0898819422");
		assertFalse(client.isPresent());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_ClientExist_ReturnErrorMessage() throws Exception {
		Client client = new Client();
		client.setEmail("projects@gmail.com");
		client.setPhone("0898819422");
		client.setFirstName("Test");
		client.setLastName("Test");
		clientRepository.saveAndFlush(client);
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects@gmail.com").param("phone", "0898819422")
						//Car
						.param("registrationNumber", "CB2125KO").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109177").param("checked", String.valueOf(true)).param("name", "Audi").param("uic", "201799236").param("vatNumber", "BG201799236").param("address", "1HGBH41JXMN109177").param("accountablePerson", "Test test")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/add"));
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_CarExistAndClientIsNotNull_ReturnErrorMessage() throws Exception {
		Car car = new Car();
		car.setKw(120);
		car.setHp(120);
		car.setCube(1200);
		car.setRegistrationNumber("CB2125KO");
		car.setModel("AUDI");
		car.setMake("AUDI");
		car.setVin("1HGBH41JXMN109177");
		car.setYear(2000);
		car.setClient(clientFirst);
		
		carRepository.saveAndFlush(car);
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects1@gmail.com").param("phone", "0898819433").param("registrationNumber", "CB2125KO").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109177").param("clientId", String.valueOf(car.getClient().getId())).param("checked", String.valueOf(true)).param("name", "Audi").param("uic", "201799236").param("vatNumber", "BG201799236").param("address", "1HGBH41JXMN109177").param("accountablePerson", "Test test")
						
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/add"));
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createClient_CompanyExistAndClientIsNotNull_ReturnErrorMessage() throws Exception {
		Client client = new Client();
		client.setPhone("0796556952");
		client.setEmail("test7@testt.bg");
		client.setFirstName("Test");
		client.setLastName("Test");
		clientRepository.saveAndFlush(client);
		Company company = new Company();
		company.setName("Audi");
		company.setUic("201799236");
		company.setVatNumber("BG201799236");
		company.setAddress("1HGBH41JXMN109177");
		company.setAccountablePerson("Test test");
		company.setClient(client);
		HashMap<String, String> apiResp = new HashMap<>();
		apiResp.put("status", "success");
		when(apiFeignClient.saveMakeAndModel(any(SaveMakeModelDto.class))).thenReturn(ResponseEntity.ok(apiResp));
		
		companyRepository.saveAndFlush(company);
		
		mockMvc.perform(post("/client/add")
						
						.param("firstName", "Test").param("lastName", "Testov").param("email", "projects_@gmail.com").param("phone", "0898819412").param("registrationNumber", "CB2625KO").param("cube", String.valueOf(1200)).param("year", String.valueOf(2020)).param("hp", String.valueOf(120)).param("make", "Audi").param("model", "tt").param("kw", String.valueOf(120)).param("vin", "1HGBH41JXMN109167").param("checked", String.valueOf(true)).param("name", "Audi").param("uic", "201799236").param("vatNumber", "BG201799236").param("address", "1HGBH41JXMN109177").param("accountablePerson", "Test test").param("clientId", String.valueOf(UUID.randomUUID()))
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/add"));
		
	}
	
	
	@Test
	@WithMockUser(username = "admin")
	public void deleteClient_WithCarAndCompany() throws Exception {
		Client client = new Client();
		client.setEmail("projects@gmail.com");
		client.setPhone("0898819422");
		client.setFirstName("Test");
		client.setLastName("Test");
		
		clientRepository.saveAndFlush(client);
		Company compnay = new Company();
		compnay.setName("Audi");
		compnay.setUic("201799236");
		compnay.setVatNumber("BG201799236");
		compnay.setAddress("1HGBH41JXMN109177");
		compnay.setAccountablePerson("Test test");
		compnay.setClient(clientFirst);
		
		
		companyRepository.saveAndFlush(compnay);
		Car car = new Car();
		car.setKw(120);
		car.setHp(120);
		car.setCube(1200);
		car.setRegistrationNumber("CB2125KO");
		car.setModel("AUDI");
		car.setMake("AUDI");
		car.setVin("1HGBH41JXMN109177");
		car.setYear(2000);
		car.setClient(clientFirst);
		
		carRepository.saveAndFlush(car);
		
		
		mockMvc.perform(post("/client/delete/{id}", clientFirst.getId())
						
						.with(user("admin").roles(UsersRole.ADMIN.toString())).param("id", String.valueOf(clientFirst.getId())).with(csrf()))
				
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/list"));
		
		
		Optional<Car> optCar = carRepository.findById(car.getId());
		assertNotNull(optCar.get().getDeletedAt());
		
		Optional<Company> optCompnay = companyRepository.findById(compnay.getId());
		assertNotNull(optCompnay.get().getDeletedAt());
		
		Optional<Client> optClient = clientRepository.findById(clientFirst.getId());
		assertNotNull(optClient.get().getDeletedAt());
	}
	
	@Test
	@WithMockUser(username = "editor", roles = "EDITOR")
	public void deleteClient_WithCarAndCompany_AccessDenied() throws Exception {
		Client client = new Client();
		client.setEmail("projects@gmail.com");
		client.setPhone("0898819422");
		client.setFirstName("Test");
		client.setLastName("Test");
		
		clientRepository.saveAndFlush(client);
		Company compnay = new Company();
		compnay.setName("Audi");
		compnay.setUic("201799236");
		compnay.setVatNumber("BG201799236");
		compnay.setAddress("1HGBH41JXMN109177");
		compnay.setAccountablePerson("Test test");
		compnay.setClient(clientFirst);
		
		
		companyRepository.saveAndFlush(compnay);
		Car car = new Car();
		car.setKw(120);
		car.setHp(120);
		car.setCube(1200);
		car.setRegistrationNumber("CB2125KO");
		car.setModel("AUDI");
		car.setMake("AUDI");
		car.setVin("1HGBH41JXMN109177");
		car.setYear(2000);
		car.setClient(clientFirst);
		
		carRepository.saveAndFlush(car);
		
		
		mockMvc.perform(post("/client/delete/{id}", clientFirst.getId())
						
						
						.param("id", String.valueOf(clientFirst.getId())).with(csrf()))
				
				.andExpect(status().isForbidden());
		
		Optional<Client> optClient = clientRepository.findById(client.getId());
		assertNull(optClient.get().getDeletedAt());
		
		Optional<Car> optCar = carRepository.findById(car.getId());
		assertNull(optCar.get().getDeletedAt());
		
		Optional<Company> optCompnay = companyRepository.findById(compnay.getId());
		assertNull(optCompnay.get().getDeletedAt());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void removeCompanyFromClient_Success() throws Exception {
		Company company = new Company();
		company.setName("Audi");
		company.setUic("201799236");
		company.setVatNumber("BG201799236");
		company.setAddress("1HGBH41JXMN109177");
		company.setAccountablePerson("Test test");
		company.setClient(clientFirst);
		companyRepository.saveAndFlush(company);
		UUID clientId = clientFirst.getId();
		UUID companyId = company.getId();
		
		
		mockMvc.perform(post("/client/remove-company/{id}", companyId).param("clientId", String.valueOf(clientId)).with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/edit/" + clientId)).andExpect(flash().attribute("status", "success")).andExpect(flash().attributeExists("message"));
		
		Optional<Company> optCompany = companyRepository.findById(companyId);
		assertTrue(optCompany.isPresent());
		assertNull(optCompany.get().getClient());
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void removeCompanyFromClient_ResponseStatusException() throws Exception {
		Company company = new Company();
		company.setName("Audi");
		company.setUic("201799236");
		company.setVatNumber("BG201799236");
		company.setAddress("1HGBH41JXMN109177");
		company.setAccountablePerson("Test test");
		company.setClient(clientFirst);
		companyRepository.saveAndFlush(company);
		UUID clientId = clientFirst.getId();
		UUID companyId = UUID.randomUUID();
		
		
		mockMvc.perform(post("/client/remove-company/{id}", companyId).param("clientId", String.valueOf(clientId)).with(csrf())).andExpect(status().is4xxClientError());
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void removeCarFromClient_Success() throws Exception {
		Car car = new Car();
		car.setRegistrationNumber("CB1234AB");
		car.setMake("Audi");
		car.setModel("A4");
		car.setYear(2020);
		car.setKw(1100);
		car.setHp(150);
		car.setCube(2000);
		car.setVin("VIN12345678901234");
		car.setClient(clientFirst);
		carRepository.saveAndFlush(car);
		UUID carId = car.getId();
		UUID clientId = clientFirst.getId();
		
		mockMvc.perform(post("/client/remove-car/{id}", carId).param("clientId", String.valueOf(clientId)).with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/client/edit/" + clientId)).andExpect(flash().attribute("status", "success")).andExpect(flash().attributeExists("message"));
		
		// Проверка в базата дали car вече не е свързан с клиента
		Optional<Car> optCar = carRepository.findById(carId);
		assertTrue(optCar.isPresent());
		assertNull(optCar.get().getClient());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editClient_ShouldReturnModelWithClientCarsAndCompanies() throws Exception {
		Optional<Client> c = clientRepository.findById(clientFirst.getId());
		assertTrue(c.isPresent());
		
		mockMvc.perform(get("/client/edit/{id}", clientFirst.getId()).contentType(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk()).andExpect(model().attributeExists("client")).andExpect(model().attributeExists("cars")).andExpect(model().attributeExists("clientId")).andExpect(model().attributeExists("companies")).andExpect(model().attributeExists("carsWithoutUser")).andExpect(model().attributeExists("companiesWithoutUser"));
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editClient_WithBindingResult_RedirectToClientEdit() throws Exception {
		
		mockMvc.perform(put("/client/edit/{id}", clientFirst.getId()).param("firstName", "Te").param("lastName", "Testov").param("email", "projects45@gmail.com").param("phone", "0898819465").with(csrf())).andExpect(status().is3xxRedirection());
		
		
	}
	
	@Test
	@WithAnonymousUser
	public void editClient_AccessDenied() throws Exception {
		mockMvc.perform(put("/client/edit/{id}", clientFirst.getId()).param("firstName", "Test").param("lastName", "Testov").param("email", "projects45@gmail.com").param("phone", "0898819465").with(csrf())).andExpect(status().isForbidden());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void fetchAllClientsByDeletedAtNull_ReturnList() throws Exception {
		clientSecond.setDeletedAt(LocalDateTime.now());
		clientRepository.save(clientSecond);
		
		mockMvc.perform(get("/client/fetch/clients").contentType(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.clients").isArray()).andExpect(jsonPath("$.clients.length()").value(1)).andExpect(jsonPath("$.clients[0].firstName").value("Test"));
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void fetchCompaniesByClientId_ReturnList() throws Exception {
		
		System.out.println(clientFirst.getId());
		Company company = new Company();
		company.setClient(clientFirst);
		System.out.println(company.getClient().getId());
		company.setUic("201201201");
		company.setVatNumber("BG201201201");
		company.setName("BG201201201");
		company.setAddress("BG201201201");
		company.setAccountablePerson("BG201201201");
		companyRepository.save(company);
		mockMvc.perform(get("/client/fetch/companies/{id}", clientFirst.getId()).contentType(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.companies").isArray()).andExpect(jsonPath("$.companies.length()").value(1)).andExpect(jsonPath("$.companies[0].uic").value("201201201"));
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void fetchCompaniesByClientId_ReturnEmptyList() throws Exception {
		
		System.out.println(clientFirst.getId());
		Company company = new Company();
		company.setClient(clientFirst);
		company.setClient(clientSecond);
		System.out.println(company.getClient().getId());
		company.setUic("201201201");
		company.setVatNumber("BG201201201");
		company.setName("BG201201201");
		company.setAddress("BG201201201");
		company.setAccountablePerson("BG201201201");
		companyRepository.save(company);
		mockMvc.perform(get("/client/fetch/companies/{id}", clientFirst.getId()).contentType(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.companies").isArray()).andExpect(jsonPath("$.companies.length()").value(0));
		
	}
	
}

