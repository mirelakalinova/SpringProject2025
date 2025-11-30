package com.example.mkalinova.order;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarServiceImpl;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.client.service.ClientServiceImpl;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyServiceImpl;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.order.repo.OrderRepository;
import com.example.mkalinova.app.order.service.OrderServiceImpl;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderPart.service.OrderPartService;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import com.example.mkalinova.app.orderRepair.service.OrderRepairService;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderUTest {
	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private CarRepository carRepository;
	@Mock
	private CompanyRepository companyRepository;
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private ModelMapper modelMapper;
	@Mock
	private OrderPartService orderPartService;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private OrderRepairService orderRepairService;
	@Mock
	private PartRepository partRepository;
	@Mock
	private RepairRepository repairRepository;
	@Mock
	private CompanyServiceImpl companyService;
	@Mock
	private CarServiceImpl carService;
	@Mock
	private ClientServiceImpl clientService;
	@InjectMocks
	private OrderServiceImpl service;
	
	private Order order;
	private User admin;
	private User editor;
	private Company company;
	private Client client;
	private Car car;
	private Part part;
	private Repair repair;
	
	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
		userRepository.deleteAll();
		clientRepository.deleteAll();
		companyRepository.deleteAll();
		carRepository.deleteAll();
		partRepository.deleteAll();
		
		repairRepository.deleteAll();
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
		order = new Order();
		order.setCreatedAt(LocalDateTime.now());
		order.setCompany(new Company());
		order.setClient(new Client());
		order.setDiscount(12D);
		order.setPartList(List.of(new OrderPart()));
		order.setRepairList(List.of(new OrderRepair()));
		order.setSubtotal(125);
		order.setTotal(113);
		order.setTax(12);
		order.setId(UUID.randomUUID());
		order.setDiscountAmount(0D);
		order.setDiscountPercent(0D);
		orderRepository.save(order);
		company = new Company();
		company.setAddress("Test");
		company.setUic("201799235");
		company.setVatNumber("BG201799235");
		company.setName("Test");
		company.setAccountablePerson("Test test");
		company.setId(UUID.randomUUID());
		companyRepository.save(company);
		
		client = new Client();
		client.setPhone("0896619422");
		client.setLastName("Test");
		client.setFirstName("Test");
		client.setId(UUID.randomUUID());
		clientRepository.save(client);
		
		car = new Car();
		car.setYear(2020);
		car.setVin("20ds45ds45ds12ds4");
		car.setMake("Test");
		car.setModel("test");
		car.setCube(2000);
		car.setKw(120);
		car.setHp(120);
		car.setRegistrationNumber("CB2126KH");
		car.setId(UUID.randomUUID());
		carRepository.save(car);
		
		part = new Part();
		part.setPrice(120);
		part.setId(UUID.randomUUID());
		part.setName("test");
		partRepository.save(part);
		
		repair = new Repair();
		repair.setPrice(120);
		repair.setId(UUID.randomUUID());
		repair.setName("test");
		repairRepository.save(repair);
		
		
	}
	
	@Test
	void saveOrder_AccessDenied() throws AccessDeniedException {
		AddOrderDto dto = new AddOrderDto();
		Order newOrder = modelMapper.map(dto, Order.class);
		doThrow(new AccessDeniedException("Нямате права да извършите тази операция!"))
				.when(userService).isUserLogIn();
		assertThrows(AccessDeniedException.class, () -> service.saveOrder(dto));
		
		verify(orderRepository, never()).save(newOrder);
		verify(orderRepairService, never()).saveOrderRepair(new AddOrderRepairDto(), order);
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void saveOrderWithNonExistingCar_ErrorMessage() throws AccessDeniedException {
		AddOrderDto dto = getAddOrderDto(UUID.randomUUID(), client.getId(), company.getId());
		
		Order newOrder = modelMapper.map(dto, Order.class);
		doNothing().when(userService).isUserLogIn();
		
		when(modelMapper.map(dto, Order.class)).thenReturn(new Order());
		when(carService.getById(dto.getCar(), Car.class)).thenReturn(null);
		
		HashMap<String, String> result = service.saveOrder(dto);
		
		verify(orderRepository, never()).save(modelMapper.map(dto, Order.class));
		
		assertEquals("error", result.get("status"));
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void saveOrderWithNonExistingCompany_ErrorMessage() throws AccessDeniedException {
		AddOrderDto dto = getAddOrderDto(car.getId(), client.getId(), UUID.randomUUID());
		
		Order newOrder = modelMapper.map(dto, Order.class);
		doNothing().when(userService).isUserLogIn();
		
		when(modelMapper.map(dto, Order.class)).thenReturn(new Order());
		when(carService.getById(dto.getCar(), Car.class)).thenReturn(car);
		when(clientService.getById(dto.getClient())).thenReturn(Optional.of(client));
		when(companyService.getById(dto.getCompany(), Company.class)).thenReturn(null);
		
		HashMap<String, String> result = service.saveOrder(dto);
		
		verify(orderRepository, never()).save(modelMapper.map(dto, Order.class));
		
		assertEquals("error", result.get("status"));
		
	}
	
	private AddOrderDto getAddOrderDto(UUID car, UUID client, UUID company) {
		AddOrderDto dto = new AddOrderDto();
		dto.setCar(car);
		dto.setClient(client);
		dto.setCompany(company);
		dto.setDiscount(20);
		dto.setSubtotal(120);
		dto.setTotal(140);
		dto.setTax(20);
		AddOrderPartDto addOrderPartDto = new AddOrderPartDto();
		addOrderPartDto.setName("test");
		addOrderPartDto.setPrice(120D);
		addOrderPartDto.setQuantity(60);
		addOrderPartDto.setQuantity(2);
		AddOrderRepairDto addOrderRepairDto = new AddOrderRepairDto();
		addOrderRepairDto.setName("test");
		addOrderRepairDto.setPrice(120D);
		addOrderRepairDto.setQuantity(60);
		addOrderRepairDto.setQuantity(2);
		
		dto.setParts(List.of(addOrderPartDto));
		dto.setRepairs(List.of(addOrderRepairDto));
		return dto;
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void saveOrderWithNonExistingClient_ErrorMessage() throws AccessDeniedException {
		AddOrderDto dto = getAddOrderDto(car.getId(), UUID.randomUUID(), company.getId());
		
		Order newOrder = modelMapper.map(dto, Order.class);
		doNothing().when(userService).isUserLogIn();
		
		when(modelMapper.map(dto, Order.class)).thenReturn(new Order());
		when(carService.getById(dto.getCar(), Car.class)).thenReturn(car);
		when(clientService.getById(dto.getClient())).thenReturn(Optional.empty());
		
		
		HashMap<String, String> result = service.saveOrder(dto);
		
		verify(orderRepository, never()).save(modelMapper.map(dto, Order.class));
		
		assertEquals("error", result.get("status"));
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void saveOrder_Success() throws AccessDeniedException {
		AddOrderDto dto = getAddOrderDto(car.getId(), client.getId(), company.getId());
		
		Order newOrder = modelMapper.map(dto, Order.class);
		doNothing().when(userService).isUserLogIn();
		
		when(modelMapper.map(dto, Order.class)).thenReturn(new Order());
		when(carService.getById(car.getId(), Car.class)).thenReturn(car);
		when(clientService.getById(dto.getClient())).thenReturn(Optional.of(client));
		when(companyService.getById(dto.getCompany(), Company.class)).thenReturn(company);
		
		HashMap<String, String> result = service.saveOrder(dto);
		
		verify(orderRepository).save(modelMapper.map(dto, Order.class));
		
		assertEquals("success", result.get("status"));
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void orderList_Success() throws AccessDeniedException {
		
		OrderListDto dto = new OrderListDto();
		when(modelMapper.map(order, OrderListDto.class)).thenReturn(dto);
		dto = modelMapper.map(order, OrderListDto.class);
		
		order.setDeletedAt(LocalDateTime.now());
		
		OrderListDto deletedDto = modelMapper.map(order, OrderListDto.class);
		List<OrderListDto> dtoList = new ArrayList<>();
		dtoList.add(dto);
		dtoList.add(deletedDto);
		doNothing().when(userService).isUserLogIn();
		when(orderRepository.findAllByDeletedAtNull()).thenReturn(List.of(order));
		
		
		List<OrderListDto> result = service.getAllOrders();
		
		assertNotEquals(dtoList.size(), result.size());
		assertEquals(1, result.size());
		
		
	}
	
	@Test
	@WithAnonymousUser
	void orderList_AccessDenied() throws AccessDeniedException {
		
		doThrow(new AccessDeniedException("Нямате права да извършите тази операция!"))
				.when(userService).isUserLogIn();
		assertThrows(AccessDeniedException.class, () -> service.getAllOrders());
		
		
	}
	
	@Test
	void editOrder_AccessDenied() throws AccessDeniedException {
		EditOrderDto dto = new EditOrderDto();
		dto.setId(UUID.randomUUID());
		Order newOrder = modelMapper.map(dto, Order.class);
		doThrow(new AccessDeniedException("Нямате права да извършите тази операция!"))
				.when(userService).isUserLogIn();
		assertThrows(AccessDeniedException.class, () -> service.editOrder(dto.getId(), dto));
		
		verify(orderRepairService, never()).deleteAllByOrderId(any());
		verify(orderRepairService, never()).saveOrderRepair(new AddOrderRepairDto(), order);
		verify(orderPartService, never()).deletedAllByOrderId(any());
		verify(orderPartService, never()).saveOrderPart(new AddOrderPartDto(), order);
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void editOrder_Success() throws AccessDeniedException {
		
		UUID orderId = UUID.randomUUID();
		UUID carId = UUID.randomUUID();
		UUID clientId = UUID.randomUUID();
		UUID companyId = UUID.randomUUID();
		
		Order order = new Order();
		order.setId(orderId);
		
		Car newCar = new Car();
		newCar.setId(carId);
		Client newClient = new Client();
		newClient.setId(clientId);
		Company newCompany = new Company();
		newCompany.setId(companyId);
		
		EditOrderDto dto = new EditOrderDto();
		dto.setCar(newCar);
		dto.setClient(newClient);
		dto.setCompany(newCompany);
		dto.setParts(List.of(new AddOrderPartDto()));
		dto.setRepairs(List.of(new AddOrderRepairDto()));
		
		
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(carService.getById(carId, Car.class)).thenReturn(newCar);
		when(clientService.getById(clientId)).thenReturn(Optional.of(newClient));
		when(companyService.getById(companyId, Company.class)).thenReturn(newCompany);
		
		
		HashMap<String, String> result = service.editOrder(orderId, dto);
		
		
		assertEquals("success", result.get("status"));
		assertEquals("Успешно добавен ремонт!", result.get("message"));
		verify(orderPartService, atLeastOnce()).saveOrderPart(any(), any());
		verify(orderRepairService, atLeastOnce()).saveOrderRepair(any(), any());
		verify(orderRepository).save(order);
		assertNotNull(order.getEditedAt());
		assertEquals(newCar, order.getCar());
		assertEquals(newClient, order.getClient());
		assertEquals(newCompany, order.getCompany());
	}
	
	@Test
	void EditOrder_NotFoundException() {
		UUID orderId = UUID.randomUUID();
		EditOrderDto dto = new EditOrderDto();
		
		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
		
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
				service.editOrder(orderId, dto));
		
		assertEquals("Поръчка с #" + orderId + " не съществува!", ex.getReason());
	}
}
