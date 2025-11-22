package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.order.repo.OrderRepository;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderPart.service.OrderPartService;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import com.example.mkalinova.app.orderRepair.service.OrderRepairService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CarService carService;
    private final CompanyService companyService;
    private final ClientService clientService;
    private final ModelMapper modelMapper;
    private final OrderPartService orderPartService;
    private final OrderRepairService orderRepairService;

    public OrderServiceImpl(OrderRepository orderRepository, CarService carService, CompanyService companyService, ClientService clientService, ModelMapper modelMapper, OrderPartService orderPartService, OrderRepairService orderRepairService) {
        this.orderRepository = orderRepository;
        this.carService = carService;
        this.companyService = companyService;
        this.clientService = clientService;
        this.modelMapper = modelMapper;
        this.orderPartService = orderPartService;
        this.orderRepairService = orderRepairService;
    }

    @Override
    @Transactional
    public HashMap<String, String> saveOrder(AddOrderDto orderDto) {
        //todo -> add isUserLogin();
        HashMap<String, String> result = new HashMap<>();
        Order order = modelMapper.map(orderDto, Order.class);
        if (orderDto.getCar() > 0) {
            Car car = (Car) carService.getById(orderDto.getCar(), Car.class);
            if (car == null) {
                result.put("status", "error");
                result.put("message", "Кола с #" + orderDto.getCar() + " не съществува!");
                return result;
            }
            order.setCar(car);
        }

        if (orderDto.getClient() > 0) {
            Optional<Client> client = clientService.getById(orderDto.getClient());
            if (client.isEmpty()) {
                result.put("status", "error");
                result.put("message", "Клиент с #" + orderDto.getCar() + " не съществува!");
                return result;
            }
            order.setClient(client.get());
        }
        if (orderDto.getCompany() > 0) {
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
    public List<OrderListDto> getAllOrders() {
        List<Order> orderList = orderRepository.findAllByDeletedAtNull();
        //map to OrderListDto
        List<OrderListDto> listDto = orderList.stream().map(o -> modelMapper.map(o, OrderListDto.class)).toList();
        listDto.forEach(o -> {
            Long id = o.getId();
            //add all parts
            orderPartService.findAllByOrderId(id).forEach(p -> o.getPartsList().add(p));
            //add all repairs
            orderRepairService.findAllByOrderId(id).forEach(r -> o.getRepairsList().add(r));
        });
        return listDto;
    }

    @Override
    public HashMap<String, String> editOrder(Long id) {
        return null;
    }

    @Override
    public HashMap<String, String> deleteOrder(Long id) {
        return null;
    }


}
