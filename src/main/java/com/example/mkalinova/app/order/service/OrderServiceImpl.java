package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.service.CompanyService;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
        userService.isUserLogIn();

        HashMap<String, String> result = new HashMap<>();
        Order order = modelMapper.map(orderDto, Order.class);

        if (orderDto.getCar() !=null) {
            Car car = (Car) carService.getById(orderDto.getCar(), Car.class);
            if (car == null) {
                result.put("status", "error");
                result.put("message", "Кола с #" + orderDto.getCar() + " не съществува!");
                return result;
            }
            order.setCar(car);
        }

        if (orderDto.getClient() != null) {
            Optional<Client> client = clientService.getById(orderDto.getClient());
            if (client.isEmpty()) {
                result.put("status", "error");
                result.put("message", "Клиент с #" + orderDto.getCar() + " не съществува!");
                return result;
            }
            order.setClient(client.get());
        }
        if (orderDto.getCompany() !=null) {
            Company company = (Company) companyService.getById(orderDto.getCompany(), Company.class);
            if (company == null) {
                result.put("status", "error");
                result.put("message", "Фирма с #" + orderDto.getCar() + " не съществува!");
                return result;
            }

            order.setCompany(company);
        }


        order.setDate(LocalDateTime.now());
        orderRepository.save(order);

        List<AddOrderPartDto> partList = orderDto.getParts();
        partList.forEach(p -> orderPartService.saveOrderPart(p, order));

        List<AddOrderRepairDto> repairList = orderDto.getRepairs();
        repairList.forEach(r -> orderRepairService.saveOrderRepair(r, order));

        result.put("status", "success");
        result.put("message", "Успешно добавен ремонт!");
        return result;
    }


    @Override
    public List<OrderListDto> getAllOrders() throws AccessDeniedException {
        userService.isUserLogIn();
        List<Order> orderList = orderRepository.findAllByDeletedAtNull();
        //map to OrderListDto
        List<OrderListDto> listDto = orderList.stream().map(o -> modelMapper.map(o, OrderListDto.class)).toList();
        listDto.forEach(o -> {
            UUID id = o.getId();
            //add all parts
            orderPartService.findAllByOrderId(id).forEach(p -> o.getPartsList().add(p));
            //add all repairs
            orderRepairService.findAllByOrderId(id).forEach(r -> o.getRepairsList().add(r));
        });
        return listDto;
    }

    @Override
    public EditOrderDto getOrderById(UUID id) {

        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Поръчка с #" + id + " не съществува!");
        }
        List<OrderPart> orderParts = orderPartService.findAllByOrderId(id);
        if (!orderParts.isEmpty()) {
            order.get().setPartList(orderParts);
        }
        List<OrderRepair> orderRepairs = orderRepairService.findAllByOrderId(id);
        if (!orderRepairs.isEmpty()) {
            order.get().setRepairList(orderRepairs);
        }
        System.out.println();
        return modelMapper.map(orderRepository.findByIdAndDeletedAtIsNull(id), EditOrderDto.class);
    }

    @Override
    public HashMap<String, String> editOrder(UUID id, EditOrderDto dto) throws AccessDeniedException {
        userService.isUserLogIn();
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Поръчка с #" + id + " не съществува!");
        }
        HashMap<String, String> result = new HashMap<>();

        //delete na всички части и извършени ремонти
        orderPartService.deletedAllByOrderId(id);
        orderRepairService.deleteAllByOrderId(id);
        Order newOrderData = modelMapper.map(dto, Order.class); // todo -> трябва ли ми ?

        if (dto.getCar().getId() !=null) {
            Car car = (Car) carService.getById(dto.getCar().getId(), Car.class);
            if (car == null) {
                result.put("status", "error");
                result.put("message", "Кола с #" + dto.getCar().getId() + " не съществува!");
                return result;
            }
            order.get().setCar(car);
        }

        if (dto.getClient().getId() !=null) {
            Optional<Client> client = clientService.getById(dto.getClient().getId());
            if (client.isEmpty()) {
                result.put("status", "error");
                result.put("message", "Клиент с #" + dto.getCar().getId() + " не съществува!");
                return result;
            }
            order.get().setClient(client.get());
        }
        if (dto.getCompany().getId() != null ) {
            Company company = (Company) companyService.getById(dto.getCompany().getId(), Company.class);
            if (company == null) {
                result.put("status", "error");
                result.put("message", "Фирма с #" + dto.getCar().getId() + " не съществува!");
                return result;
            }

            order.get().setCompany(company);
        }


        order.get().setEditedAt(LocalDateTime.now());
        orderRepository.save(order.get());

        List<AddOrderPartDto> partList = dto.getParts();
        partList.forEach(p -> orderPartService.saveOrderPart(p, order.get()));

        List<AddOrderRepairDto> repairList = dto.getRepairs();
        repairList.forEach(r -> orderRepairService.saveOrderRepair(r, order.get()));

        result.put("status", "success");
        result.put("message", "Успешно добавен ремонт!");
        // save на новите
        return result;
    }

    @Override
    @Transactional
    public HashMap<String, String> deleteOrder(UUID id) {
        HashMap<String, String> result = new HashMap<>();
        try {
            Optional<Order> orderToDelete = orderRepository.findById(id);

            if (orderToDelete.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма намерена поръчка с #" + id);
            }
            orderPartService.setDeletedAtAllByOrderId(id);
            orderRepairService.setDeletedAtAllByOrderId(id);
            orderToDelete.get().setDeletedAt(LocalDateTime.now());
            orderRepository.save(orderToDelete.get());
            result.put("status", "success");
            result.put("message", "Успешно изтрита поръчка с # " + id);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            result.put("status", "error");
            result.put("message", e.getMessage());
            return result;
        }
    }


}
