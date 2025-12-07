package com.example.mkalinova.app.repair.service;


import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.repair.data.dto.EditRepairDto;
import com.example.mkalinova.app.repair.data.dto.RepairDto;
import com.example.mkalinova.app.repair.data.dto.RepairListDto;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
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
public class RepairServiceServiceImpl implements RepairService {
	private final RepairRepository carServiceRepository;
	private final ModelMapper modelMapper;
	private final UserService userService;
	
	public RepairServiceServiceImpl(RepairRepository carServiceRepository, ModelMapper modelMapper, UserService userService) {
		this.carServiceRepository = carServiceRepository;
		this.modelMapper = modelMapper;
		this.userService = userService;
	}
	
	
	@Override
	public List<RepairListDto> getAllServicesByDeletedAtNull() {
		log.debug("Attempt to get all active repairs..");
		
		return this.carServiceRepository.findAllByDeletedAtNull().stream().map(p -> modelMapper.map(p, RepairListDto.class)).toList();
	}
	
	@Override
	public HashMap<String, String> addService(RepairDto dto) throws AccessDeniedException {
		log.debug("Attempt to add repair with name {}", dto.getName());
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		Optional<Repair> optService = carServiceRepository.findByName(dto.getName());
		if (optService.isPresent()) {
			result.put("status", "error");
			result.put("message", "Услуга " + dto.getName() + " вече съществува!");
			log.warn("Return error message - repair with name {} already exists ", dto.getName());
			
			return result;
		}
		carServiceRepository.save(modelMapper.map(dto, Repair.class));
		result.put("status", "success");
		result.put("message", "Успешно добавена услуга " + dto.getName());
		log.info("Successfully saved repair with name {}", dto.getName());
		return result;
	}
	
	@Override
	public HashMap<String, String> deleterepair(UUID id) throws AccessDeniedException {
		userService.isUserLogIn();
		Optional<User> user = userService.getLoggedInUser();
		if (user.isPresent()) {
			if (!userService.isAdmin(user.get())) {
				throw new AccessDeniedException("Нямате права да извършите тази операция!");
			}
		}
		HashMap<String, String> result = new HashMap<>();
		Optional<Repair> service = carServiceRepository.findById(id);
		if (service.isPresent()) {
			service.get().setDeletedAt(LocalDateTime.now());
			carServiceRepository.save(service.get());
			result.put("status", "success");
			result.put("message", "Успешно изтрита услуга " + service.get().getName());
			log.info("Successfully deleted repair with id {}", id);
			
			return result;
		}
		throw new NoSuchResourceException("Услуга с #" + id + " не съществува!");
	}
	
	@Override
	public HashMap<String, String> editService(EditRepairDto dto) throws AccessDeniedException {
		log.debug("Attempt to edit repair with id {}", dto.getId());
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		Optional<Repair> optService = carServiceRepository.findById(dto.getId());
		if (optService.isEmpty()) {
	
			log.warn("Return error message: edited repair with id {} does not exist", dto.getId());
			throw new NoSuchResourceException("Услуга #" + dto.getId() + " не съществува!");
		}
		optService.get().setName(dto.getName());
		if (dto.getPrice() != null) {
			optService.get().setPrice(dto.getPrice());
		} else {
			optService.get().setPrice(0);
		}
		carServiceRepository.save(optService.get());
		result.put("status", "success");
		result.put("message", "Успешно редактирана услуга " + dto.getName());
		log.info("Successfully edited repair with id {}", dto.getId());
		
		return result;
	}
	
	@Override
	public EditRepairDto findById(UUID id) {
		log.debug("Attempt to find repair with id {}", id);
		Optional<Repair> service = carServiceRepository.findById(id);
		if (service.isPresent()) {
			log.info("Successfully find repair with id {}", id);
			return modelMapper.map(service, EditRepairDto.class);
		}
		log.warn("Return error message: repair with id {} does not exist", id);
		throw new NoSuchResourceException("Няма намерена услуга с #" + id);
	}
}
