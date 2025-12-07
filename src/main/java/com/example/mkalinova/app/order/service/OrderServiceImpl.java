package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.order.repo.OrderRepository;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderPart.service.OrderPartService;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import com.example.mkalinova.app.orderRepair.service.OrderRepairService;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
	
	private final OrderRepository orderRepository;
	private final CarService carService;
	private final CompanyService companyService;
	private final ClientService clientService;
	private final ModelMapper modelMapper;
	private final OrderPartService orderPartService;
	private final OrderRepairService orderRepairService;
	private final UserService userService;
	
	public OrderServiceImpl(OrderRepository orderRepository, CarService carService, CompanyService companyService, ClientService clientService, ModelMapper modelMapper, OrderPartService orderPartService, OrderRepairService orderRepairService, UserService userService) {
		this.orderRepository = orderRepository;
		this.carService = carService;
		this.companyService = companyService;
		this.clientService = clientService;
		this.modelMapper = modelMapper;
		this.orderPartService = orderPartService;
		this.orderRepairService = orderRepairService;
		this.userService = userService;
	}
	
	@Override
	@Transactional
	public HashMap<String, String> saveOrder(AddOrderDto orderDto) throws AccessDeniedException {
		
		log.debug("Attempt to save new order..");
		userService.isUserLogIn();
		
		HashMap<String, String> result = new HashMap<>();
		Order order = modelMapper.map(orderDto, Order.class);
		
		if (orderDto.getCar() != null) {
			Car car = (Car) carService.getById(orderDto.getCar(), Car.class);
			if (car == null) {
				result.put("status", "error");
				result.put("message", "Кола с #" + orderDto.getCar() + " не съществува!");
				log.warn("Return error message in save order: Car with id {} is not present", orderDto.getCar());
				return result;
			}
			order.setCar(car);
			log.info("Successfully added a car with id {} to the order", orderDto.getCar());
		}
		
		if (orderDto.getClient() != null) {
			Optional<Client> client = clientService.getById(orderDto.getClient());
			if (client.isEmpty()) {
				result.put("status", "error");
				result.put("message", "Клиент с #" + orderDto.getCar() + " не съществува!");
				log.warn("Return error message in save order: Client with id {} is not present", orderDto.getClient());
				return result;
			}
			order.setClient(client.get());
			log.info("Successfully added a client with id {} to the order", orderDto.getClient());
		}
		if (orderDto.getCompany() != null) {
			Company company = (Company) companyService.getById(orderDto.getCompany(), Company.class);
			if (company == null) {
				result.put("status", "error");
				result.put("message", "Фирма с #" + orderDto.getCar() + " не съществува!");
				log.warn("Return error message in save order: Company with id {} is not present", orderDto.getCompany());
				return result;
			}
			
			order.setCompany(company);
			log.info("Successfully added a company with id {} to the order", orderDto.getCompany());
		}
		
		
		order.setCreatedAt(LocalDateTime.now());
		orderRepository.save(order);
		
		List<AddOrderPartDto> partList = orderDto.getParts();
		partList.forEach(p -> orderPartService.saveOrderPart(p, order));
		
		List<AddOrderRepairDto> repairList = orderDto.getRepairs();
		repairList.forEach(r -> orderRepairService.saveOrderRepair(r, order));
		
		result.put("status", "success");
		result.put("message", "Успешно добавен ремонт!");
		log.info("Successfully added new order");
		return result;
	}
	
	
	@Override
	public List<OrderListDto> getAllOrders() throws AccessDeniedException {
		log.debug("Attempt to get all cars...");
		userService.isUserLogIn();
		List<Order> orderList = orderRepository.findAllByDeletedAtNull();
		List<OrderListDto> listDto = orderList.stream().map(o -> modelMapper.map(o, OrderListDto.class)).toList();
		listDto.forEach(o -> {
			UUID id = o.getId();
			orderPartService.findAllByOrderId(id).forEach(p -> o.getPartsList().add(p));
			orderRepairService.findAllByOrderId(id).forEach(r -> o.getRepairsList().add(r));
		});
		log.info("Successfully get all orders...");
		return listDto;
	}
	
	@Override
	public EditOrderDto getOrderById(UUID id) {
		log.debug("Attempt to get order with id {}", id);
		Optional<Order> order = orderRepository.findById(id);
		if (order.isEmpty()) {
			throw new NoSuchResourceException( "Поръчка с #" + id + " не съществува!");
		}
		List<OrderPart> orderParts = orderPartService.findAllByOrderId(id);
		if (!orderParts.isEmpty()) {
			order.get().setPartList(orderParts);
		}
		List<OrderRepair> orderRepairs = orderRepairService.findAllByOrderId(id);
		if (!orderRepairs.isEmpty()) {
			order.get().setRepairList(orderRepairs);
		}
		log.info("Successfully get order with id {}", id);
		return modelMapper.map(orderRepository.findByIdAndDeletedAtIsNull(id), EditOrderDto.class);
	}
	
	@Override
	public HashMap<String, String> editOrder(UUID id, EditOrderDto dto) throws AccessDeniedException {
		log.debug("Attempt to edit order with id {}", id);
		userService.isUserLogIn();
		Optional<Order> order = orderRepository.findById(id);
		if (order.isEmpty()) {
			throw new NoSuchResourceException( "Поръчка с #" + id + " не съществува!");
		}
		HashMap<String, String> result = new HashMap<>();
		
		orderPartService.deletedAllByOrderId(id);
		orderRepairService.deleteAllByOrderId(id);
		
		if (dto.getCar().getId() != null) {
			Car car = (Car) carService.getById(dto.getCar().getId(), Car.class);
			if (car == null) {
				result.put("status", "error");
				result.put("message", "Кола с #" + dto.getCar().getId() + " не съществува!");
				log.warn("Return error message in update order: Car with id {} is not present", dto.getCar().getId());
				return result;
			}
			order.get().setCar(car);
			log.info("Successfully set car with id {} to order with id {}", car.getId(), id);
		}
		
		if (dto.getClient().getId() != null) {
			Optional<Client> client = clientService.getById(dto.getClient().getId());
			if (client.isEmpty()) {
				result.put("status", "error");
				result.put("message", "Клиент с #" + dto.getCar().getId() + " не съществува!");
				log.warn("Return error message in update order: Client with id {} is not present", dto.getCar().getId());
				return result;
			}
			order.get().setClient(client.get());
			log.info("Successfully set client with id {} to order with id {}", client.get().getId(), id);
		}
		if (dto.getCompany().getId() != null) {
			Company company = (Company) companyService.getById(dto.getCompany().getId(), Company.class);
			if (company == null) {
				result.put("status", "error");
				result.put("message", "Фирма с #" + dto.getCar().getId() + " не съществува!");
				log.warn("Return error message in update order: Company with id {} is not present", dto.getCar().getId());
				return result;
			}
			order.get().setCompany(company);
			log.info("Successfully set company with id {} to order with id {}", company.getId(), id);
		}
		
		
		order.get().setEditedAt(LocalDateTime.now());
		orderRepository.save(order.get());
		
		List<AddOrderPartDto> partList = dto.getParts();
		partList.forEach(p -> orderPartService.saveOrderPart(p, order.get()));
		
		List<AddOrderRepairDto> repairList = dto.getRepairs();
		repairList.forEach(r -> orderRepairService.saveOrderRepair(r, order.get()));
		
		result.put("status", "success");
		result.put("message", "Успешно добавен ремонт!");
		log.info("Successfully edit order with id {}", id);
		return result;
	}
	
	@Override
	@Transactional
	public HashMap<String, String> deleteOrder(UUID id) {
		log.debug("Attempt to delete order with id {}", id);
		HashMap<String, String> result = new HashMap<>();
		try {
			Optional<Order> orderToDelete = orderRepository.findById(id);
			
			if (orderToDelete.isEmpty()) {
				throw new NoSuchResourceException( "Няма намерена поръчка с #" + id);
			}
			orderPartService.setDeletedAtAllByOrderId(id);
			orderRepairService.setDeletedAtAllByOrderId(id);
			orderToDelete.get().setDeletedAt(LocalDateTime.now());
			orderRepository.save(orderToDelete.get());
			result.put("status", "success");
			result.put("message", "Успешно изтрита поръчка с # " + id);
			log.info("Successfully deleted order with id {}", id);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("message", e.getMessage());
			return result;
		}
	}
	
	@Override
	public int cleanOrder(LocalDateTime date) {
		log.debug("Attempt to delete all old orders");
		List<Order> ordersToSetDeletedAt = orderRepository.findByDeletedAtIsNullAndEditedAtBeforeOrEditedAtIsNull(date);
		int ordersToDelete = ordersToSetDeletedAt.size();
		if (!ordersToSetDeletedAt.isEmpty()) {
			
			   ordersToSetDeletedAt.forEach(o -> {
				o.setDeletedAt(LocalDateTime.now());
				orderRepository.save(o);
			});
			log.debug("Successfully deleted all old orders");
			return ordersToDelete;
		}
		log.warn("No old orders to delete!");
		return ordersToDelete;
	}
}
