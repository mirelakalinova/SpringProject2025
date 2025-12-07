package com.example.mkalinova.app.parts.service;


import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.parts.data.dto.EditPartDto;
import com.example.mkalinova.app.parts.data.dto.PartDto;
import com.example.mkalinova.app.parts.data.dto.PartListDto;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
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
public class PartServiceImpl implements PartService {
	private final PartRepository partRepository;
	private final ModelMapper modelMapper;
	private final UserService userService;
	
	public PartServiceImpl(PartRepository partRepository, ModelMapper modelMapper, UserService userService) {
		this.partRepository = partRepository;
		this.modelMapper = modelMapper;
		this.userService = userService;
	}
	
	@Override
	public List<PartListDto> getAllPartsByDeletedAtNull() {
		log.debug("Attempt to get all active parts ");
		
		return this.partRepository.findAllByDeletedAtNull().stream().map(p -> modelMapper.map(p, PartListDto.class)).toList();
	}
	
	@Override
	public HashMap<String, String> addPart(PartDto partDto) throws AccessDeniedException {
		log.debug("Attempt to add part ith name {}", partDto.getName());
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		Optional<Part> optPart = partRepository.findByName(partDto.getName());
		if (optPart.isPresent()) {
			result.put("status", "error");
			result.put("message", "Част " + partDto.getName() + " вече съществува!");
			log.warn("Return error message: Part with name {} already exists", partDto.getName());
			return result;
		}
		partRepository.save(modelMapper.map(partDto, Part.class));
		result.put("status", "success");
		result.put("message", "Успешно добавена част " + partDto.getName());
		log.info("Successfully saved part with name {}", partDto.getName());
		
		return result;
	}
	
	@Override
	public HashMap<String, String> deletePart(UUID id) throws AccessDeniedException {
		log.debug("Attempt to delete part with id {}", id);
		userService.isUserLogIn();
		Optional<User> user = userService.getLoggedInUser();
		if (user.isPresent()) {
			if (!userService.isAdmin(user.get())) {
				throw new AccessDeniedException("Нямате права да извършите тази операция!");
			}
		}
		HashMap<String, String> result = new HashMap<>();
		Optional<Part> part = partRepository.findById(id);
		if (part.isPresent()) {
			part.get().setDeletedAt(LocalDateTime.now());
			partRepository.save(part.get());
			result.put("status", "success");
			result.put("message", "Успешно изтрита част " + part.get().getName());
			log.info("Successfully deleted part with id {}", id);
			
			return result;
		}
		throw new NoSuchResourceException("Част с #" + id + " не съществува!");
	}
	
	@Override
	public HashMap<String, String> editPart(EditPartDto partDto) throws AccessDeniedException {
		log.debug("Attempt to edit part with id {}", partDto.getId());
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		Optional<Part> optPart = partRepository.findById(partDto.getId());
		if (optPart.isEmpty()) {
			log.warn("Return error message: Part with name {} does not exist", partDto.getName());
			throw new NoSuchResourceException("Част #" + partDto.getId() + " не съществува!");
			
		}
		optPart.get().setName(partDto.getName());
		if (partDto.getPrice() != null) {
			optPart.get().setPrice(partDto.getPrice());
		} else {
			optPart.get().setPrice(0);
		}
		partRepository.save(optPart.get());
		result.put("status", "success");
		result.put("message", "Успешно редактирана част " + partDto.getName());
		log.info("Successfully edited part with id {}", partDto.getId());
		return result;
	}
	
	@Override
	public EditPartDto findById(UUID id) {
		
		log.debug("Attempt to find part with id {}", id);
		Optional<Part> part = partRepository.findById(id);
		if (part.isPresent()) {
			log.info("Successfully find part with id {}", id);
			return modelMapper.map(part, EditPartDto.class);
		}
		
		throw new NoSuchResourceException("Няма намерена част с #" + id);
	}
}
