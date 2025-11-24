package com.example.mkalinova.app.orderRepair.service;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import com.example.mkalinova.app.orderRepair.repo.OrderRepairRepository;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderRepairServiceImpl implements OrderRepairService {
    private final OrderRepairRepository repository;
    private final ModelMapper modelMapper;
    private final RepairRepository repairRepository;

    public OrderRepairServiceImpl(OrderRepairRepository orderRepairRepository, ModelMapper modelMapper, RepairRepository repairRepository) {
        this.repository = orderRepairRepository;
        this.modelMapper = modelMapper;
        this.repairRepository = repairRepository;
    }

    @Override
    public void saveOrderRepair(AddOrderRepairDto addOrderRepairDto, Order order) {
        String name = addOrderRepairDto.getName();

        addOrderRepairDto.setId(null);

        OrderRepair orderRepair = modelMapper.map(addOrderRepairDto, OrderRepair.class);
        Optional<Repair> repair = repairRepository.findByName(name);
        if (repair.isPresent()) {

            orderRepair.setRepair(repair.get());
            orderRepair.setOrder(order);
            repository.saveAndFlush(orderRepair);
        } else {
            throw new RuntimeException("Нещо се обърка!");
        }
    }

    @Override
    public List<OrderRepair> findAllByOrderId(Long id) {
        return repository.findAllByOrderId(id);
    }

    @Override
    public void setDeletedAtAllByOrderId(Long id) {
        List<OrderRepair> orderRepairs = repository.findAllByOrderId(id);
       if(orderRepairs.isEmpty()){
           return;
       }
        orderRepairs.forEach(r ->{
            r.setDeletedAt(LocalDateTime.now());
            repository.save(r);
        });
    }

    @Override
    public void deleteAllByOrderId(Long id) {
        List<OrderRepair> orderRepairs = repository.findAllByOrderId(id);
        if(orderRepairs.isEmpty()){
            return;
        }
        orderRepairs.forEach(repository::delete);
    }
}
