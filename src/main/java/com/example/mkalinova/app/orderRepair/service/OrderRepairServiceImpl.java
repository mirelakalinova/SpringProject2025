package com.example.mkalinova.app.orderRepair.service;

import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import com.example.mkalinova.app.orderRepair.repo.OrderRepairRepository;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
		log.debug("Attempt to save repair to order with id {}", order.getId());
		
		String name = addOrderRepairDto.getName();
		
		addOrderRepairDto.setId(null);
		
		OrderRepair orderRepair = modelMapper.map(addOrderRepairDto, OrderRepair.class);
		Optional<Repair> repair = repairRepository.findByName(name);
		if (repair.isPresent()) {
			
			orderRepair.setRepair(repair.get());
			orderRepair.setOrder(order);
			repository.saveAndFlush(orderRepair);
			log.info("Successfully saved repair to order with id {}", order.getId());
			
		} else {
			throw new RuntimeException("Нещо се обърка!");
		}
	}
	
	@Override
	public List<OrderRepair> findAllByOrderId(UUID id) {
		log.debug("Attempt to find all repairs with order with id {}", id);
		return repository.findAllByOrderId(id);
	}
	
	@Override
	public void setDeletedAtAllByOrderId(UUID id) {
		log.debug("Attempt to safe delete all repairs of order with id {}", id);
		
		List<OrderRepair> orderRepairs = repository.findAllByOrderId(id);
		if (orderRepairs.isEmpty()) {
			throw new NoSuchResourceException("Ремонт към поръча с #" + id + " не съществува!");
		}
		orderRepairs.forEach(r -> {
			r.setDeletedAt(LocalDateTime.now());
			repository.save(r);
			log.info("Successfully safe deleted all repairs of order with id {}", id);
		});
	}
	
	@Override
	public void deleteAllByOrderId(UUID id) {
		log.debug("Attempt to delete all repairs of order with id {}", id);
		
		List<OrderRepair> orderRepairs = repository.findAllByOrderId(id);
		if (orderRepairs.isEmpty()) {
			throw new NoSuchResourceException("Ремонт към поръча с #" + id + " не съществува!");
		}
		orderRepairs.forEach(repository::delete);
		log.info("Successfully deleted all repairs of order with id {}", id);
		
	}
}
