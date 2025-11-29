package com.example.mkalinova.client;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarServiceImpl;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.client.service.ClientServiceImpl;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyServiceImpl;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientUTest {
	private final AddCarDto addCarDto = new AddCarDto();
	@Mock
	private ModelMapper modelMapper;
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private CarRepository carRepository;
	@Mock
	private CompanyRepository companyRepository;
	@InjectMocks
	private ClientServiceImpl clientService;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CarServiceImpl carService;
	@Mock
	private CompanyServiceImpl companyService;
	private Client client;
	private Client deletedClient;
	private User admin;
	private User editor;
	
	@BeforeEach
	void SetUp() {
		clientRepository.deleteAll();
		client = new Client();
		client.setEmail("test@test.bg");
		client.setFirstName("Test");
		client.setLastName("Testov");
		client.setPhone("0896619422");
		
		clientRepository.save(client);
		
		deletedClient = new Client();
		deletedClient.setEmail("test2@test.bg");
		deletedClient.setFirstName("Test");
		deletedClient.setLastName("Testov");
		deletedClient.setPhone("0896619424");
		deletedClient.setDeleteAd(LocalDateTime.now());
		clientRepository.save(deletedClient);
		
		userRepository.deleteAll();
		admin = new User();
		admin.setFirstName("Mirela");
		admin.setLastName("Kalinova");
		admin.setUsername("admin");
		admin.setEmail("admin@test.bg");
		admin.setPassword("Password1234!");
		admin.setRole(UsersRole.ADMIN);
		
		editor = new User();
		editor.setFirstName("Mirela");
		editor.setLastName("Kalinova");
		editor.setUsername("editor");
		editor.setEmail("editor@test.bg");
		editor.setPassword("Password1234!");
		editor.setRole(UsersRole.EDITOR);
		userRepository.saveAndFlush(admin);
	}
	
	@Test
	@WithAnonymousUser
	void addClientWithAdditionalData_ReturnAccessDenied() throws AccessDeniedException {
		doThrow(new AccessDeniedException("Нямате права да извършите тази опреация!")).when(userService).isUserLogIn();
		
		assertThrows(AccessDeniedException.class, () -> {
			userService.isUserLogIn();
		});
		
		// (по желание) Проверяваме, че методът е извикан точно веднъж
		verify(userService, times(1)).isUserLogIn();
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void addClientWithAdditionalData_ReturnErrorMessage() throws AccessDeniedException {
		doNothing().when(userService).isUserLogIn();
		AddClientDto dtoClient = new AddClientDto();
		dtoClient.setPhone(client.getPhone());
		dtoClient.setEmail(client.getEmail());
		dtoClient.setFirstName(client.getFirstName());
		dtoClient.setLastName(client.getLastName());
		when(clientRepository.findByPhone(client.getPhone())).thenReturn(Optional.of(client));
		HashMap<String, String> result = clientService.addClientWithAdditionalData(dtoClient, null, null, false);
		assertEquals(result.get("status"), "error");
		assertEquals(result.get("message"), "Клиент с тел. номер:" + client.getPhone() + " вече съществува!");
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void addClientWithAdditionalData_Client_ReturnSuccessMessage()
			throws AccessDeniedException {
		doNothing().when(userService).isUserLogIn();
		AddClientDto dtoClient = new AddClientDto();
		dtoClient.setPhone(client.getPhone());
		dtoClient.setEmail(client.getEmail());
		dtoClient.setFirstName(client.getFirstName());
		dtoClient.setLastName(client.getLastName());
		
		
		when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
		when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
		HashMap<String, String> result =
				clientService
						.addClientWithAdditionalData(dtoClient, null, null, false);
		
		assertEquals("success", result.get("status"));
		assertEquals("0896619422", dtoClient.getPhone());
		verifyNoInteractions(carRepository);
		verifyNoInteractions(companyRepository);
		verify(userService).isUserLogIn();
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void addClientWithAdditionalData_ClientAndExistingCarWithoutClient_ReturnSuccessMessage()
			throws AccessDeniedException {
		AddClientDto dtoClient = new AddClientDto();
		dtoClient.setPhone("0896619445");
		dtoClient.setEmail("test3@abv.bg");
		dtoClient.setFirstName(client.getFirstName());
		dtoClient.setLastName(client.getLastName());
		addCarDto.setCube(1200);
		addCarDto.setMake("audi");
		addCarDto.setModel("tt");
		addCarDto.setYear(2000);
		addCarDto.setVin("2ds410ds0ds0ds41w");
		addCarDto.setRegistrationNumber("CB2116KH");
		addCarDto.setHp(120);
		
		
		doNothing().when(userService).isUserLogIn();
		UUID id = UUID.randomUUID();
		when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
		when(modelMapper.map(any(AddClientDto.class), eq(Client.class)))
				.thenReturn(new Client());
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			Client c = invocation.getArgument(0);
			c.setId(id);
			return c;
		});
		
		addCarDto.setClientId(id);
		
		HashMap<String, String> carResult = new HashMap<>();
		carResult.put("status", "success");
		carResult.put("message", "Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува");
		
		when(carService.addCarAndReturnMessage(any(AddCarDto.class))).thenReturn(carResult);
		
		HashMap<String, String> result =
				clientService
						.addClientWithAdditionalData(dtoClient, addCarDto, null, false);
		assertEquals(result.get("status"), "success");
		assertTrue(result.get("message").contains(addCarDto.getRegistrationNumber()));
		assertTrue(result.get("message").contains(dtoClient.getFirstName()));
		
		verify(carService, times(1)).addCarAndReturnMessage(any(AddCarDto.class));
		verify(companyService, times(0)).saveCompany(any(AddCompanyDto.class));
		verify(userService).isUserLogIn();
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void addClientWithAdditionalData_ClientAndExistingCarWithoutClientAndExistingCompanyWithoutClient_ReturnSuccessMessage()
			throws AccessDeniedException {
		
		AddClientDto dtoClient = new AddClientDto();
		dtoClient.setPhone("0896619445");
		dtoClient.setEmail("test3@abv.bg");
		dtoClient.setFirstName(client.getFirstName());
		dtoClient.setLastName(client.getLastName());
		addCarDto.setCube(1200);
		addCarDto.setMake("audi");
		addCarDto.setModel("tt");
		addCarDto.setYear(2000);
		addCarDto.setVin("2ds410ds0ds0ds41w");
		addCarDto.setRegistrationNumber("CB2116KH");
		addCarDto.setHp(120);
		AddCompanyDto addCompanyDto = new AddCompanyDto();
		addCompanyDto.setAddress("Bulgaria");
		addCompanyDto.setAccountablePerson("Test test3");
		addCompanyDto.setName("Test Test3");
		addCompanyDto.setUic("201799234");
		addCompanyDto.setVatNumber("BG201799234");
		
		doNothing().when(userService).isUserLogIn();
		UUID id = UUID.randomUUID();
		when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
		when(modelMapper.map(any(AddClientDto.class), eq(Client.class)))
				.thenReturn(new Client());
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			Client c = invocation.getArgument(0);
			c.setId(id);
			return c;
		});
		
		addCarDto.setClientId(id);
		addCompanyDto.setClientId(id);
		HashMap<String, String> carResult = new HashMap<>();
		carResult.put("status", "success");
		carResult.put("message", "Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува");
		HashMap<String, String> companyResult = new HashMap<>();
		companyResult.put("status", "success");
		companyResult.put("message", "Успешно добавена компания: " + addCompanyDto.getName());
		when(carService.addCarAndReturnMessage(any(AddCarDto.class))).thenReturn(carResult);
		when(companyService.saveCompany(any(AddCompanyDto.class))).thenReturn(companyResult);
		HashMap<String, String> result =
				clientService
						.addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
		assertEquals(result.get("status"), "success");
		assertTrue(result.get("message").contains(addCarDto.getRegistrationNumber()));
		assertTrue(result.get("message").contains(addCompanyDto.getName()));
		assertTrue(result.get("message").contains(dtoClient.getFirstName()));
		
		verify(carService, times(1)).addCarAndReturnMessage(any(AddCarDto.class));
		verify(companyService, times(1)).saveCompany(any(AddCompanyDto.class));
		verify(userService).isUserLogIn();
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void addClientWithAdditionalData_ClientAndExistingCarWithClientAndExistingCompanyWithoutClient_ReturnErrorMessage()
			throws AccessDeniedException {
		
		AddClientDto dtoClient = new AddClientDto();
		dtoClient.setPhone(client.getPhone());
		dtoClient.setEmail(client.getEmail());
		dtoClient.setFirstName(client.getFirstName());
		dtoClient.setLastName(client.getLastName());
		addCarDto.setCube(1200);
		addCarDto.setMake("audi");
		addCarDto.setModel("tt");
		addCarDto.setYear(2000);
		addCarDto.setVin("2ds410ds0ds0ds41w");
		addCarDto.setRegistrationNumber("CB2116KH");
		addCarDto.setHp(120);
		AddCompanyDto addCompanyDto = new AddCompanyDto();
		addCompanyDto.setAddress("Bulgaria");
		addCompanyDto.setAccountablePerson("Test test");
		addCompanyDto.setName("Test Test");
		addCompanyDto.setUic("201799235");
		addCompanyDto.setVatNumber("BG201799235");
		
		doNothing().when(userService).isUserLogIn();
		UUID id = UUID.randomUUID();
		when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
		when(modelMapper.map(any(AddClientDto.class), eq(Client.class)))
				.thenReturn(client);
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			Client c = invocation.getArgument(0);
			c.setId(id);
			return c;
		});
		
		addCarDto.setClientId(UUID.randomUUID());
		HashMap<String, String> carResult = new HashMap<>();
		carResult.put("status", "error");
		carResult.put("message", "Автомобил с рег. номер: " + addCarDto.getRegistrationNumber() + " вече съществува");
		
		when(carService.addCarAndReturnMessage(any(AddCarDto.class))).thenReturn(carResult);
		HashMap<String, String> result =
				clientService
						.addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
		assertEquals(result.get("status"), "error");
		assertTrue(clientRepository.findById(id).isEmpty());
		
		
		verify(companyService, times(0)).saveCompany(any(AddCompanyDto.class));
		verify(carService, times(1)).addCarAndReturnMessage(any(AddCarDto.class));
		verify(userService).isUserLogIn();
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deleteClient_WithAllData_Success()
			throws AccessDeniedException {
		doNothing().when(userService).isUserLogIn();
		UUID clientId = UUID.randomUUID();
		
		when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
		client.setId(clientId);
		
		List<Car> cars = new ArrayList<>();
		Car car = new Car();
		UUID carId = UUID.randomUUID();
		car.setId(carId);
		car.setRegistrationNumber("K2116KH");
		car.setClient(client);
		cars.add(car);
		
		when(carService.getAllCarByClientId(clientId)).thenReturn(cars);
		
		List<Company> companies = new ArrayList<>();
		Company company = new Company();
		company.setClient(client);
		UUID companyId = UUID.randomUUID();
		company.setId(companyId);
		companies.add(company);
		
		when(companyService.getAllCompaniesByClientId(clientId))
				.thenReturn(companies);
		clientService.deleteClient(clientId);
		assertNotNull(car.getDeletedAt(), "Car should have deletedAt set");
		assertNotNull(company.getDeletedAt(), "Company should have deleteAd set");
		assertNotNull(client.getDeleteAd(), "Client should have deleteAd set");
		verify(companyRepository, times(1)).save(company);
		verify(carRepository, times(1)).save(car);
		
		
	}
}
