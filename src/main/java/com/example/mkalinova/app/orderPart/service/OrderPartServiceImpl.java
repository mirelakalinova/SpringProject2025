package com.example.mkalinova.app.orderPart.service;

import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderPart.repo.OrderPartRepository;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
public class OrderPartServiceImpl implements OrderPartService{
    private static final Logger log = LoggerFactory.getLogger(OrderPartServiceImpl.class);
    private final OrderPartRepository repository;
    private final ModelMapper modelMapper;
    private final PartRepository partRepository;

    public OrderPartServiceImpl(OrderPartRepository repository, ModelMapper modelMapper, PartRepository partRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.partRepository = partRepository;
    }

    @Override
    public void saveOrderPart(AddOrderPartDto addOrderPartDto, Order order) {
        log.debug("Attempt to save part to order with id {}", order.getId());


        String name = addOrderPartDto.getName();

        addOrderPartDto.setId(null);

        OrderPart orderPart = modelMapper.map(addOrderPartDto, OrderPart.class);
        Optional<Part> part = partRepository.findByName(name);
        if(part.isPresent()){

            orderPart.setPart(part.get());
            orderPart.setOrder(order);
            repository.saveAndFlush(orderPart);
            log.info("Successfully saved part to order with id {}", order.getId());
        } else{
            throw new RuntimeException("Нещо се обърка!");
        }


    }

    @Override
    public List<OrderPart> findAllByOrderId(UUID id) {
        log.debug("Attempt to find all parts with order with id {}", id);
        return repository.findAllByOrderId(id);
    }

    @Override
    public void setDeletedAtAllByOrderId(UUID id) {
        log.debug("Attempt to safe delete all parts of order with id {}", id);
        List<OrderPart> orderParts = repository.findAllByOrderId(id);
        if(orderParts.isEmpty()){
            return;
        }
        orderParts.forEach(p->{
            p.setDeletedAt(LocalDateTime.now());
            repository.save(p);
        });
        log.info("Successfully safe deleted all parts of order with id {}", id);
    }

    @Override
    public void deletedAllByOrderId(UUID id) {
        log.debug("Attempt to delete all parts of order with id {}", id);
        List<OrderPart> orderParts = repository.findAllByOrderId(id);
        if(orderParts.isEmpty()){
            return;
        }
        orderParts.forEach(repository::delete);
        log.info("Successfully deleted all parts of order with id {}", id);
    }
}
