package com.example.mkalinova.app.repair.service;


import com.example.mkalinova.app.repair.data.dto.RepairDto;
import com.example.mkalinova.app.repair.data.dto.EditRepairDto;
import com.example.mkalinova.app.repair.data.dto.RepairListDto;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
import com.example.mkalinova.app.user.service.UserService;

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
public class CarServiceServiceImpl implements RepairService {
    private final RepairRepository carServiceRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public CarServiceServiceImpl(RepairRepository carServiceRepository, ModelMapper modelMapper, UserService userService) {
        this.carServiceRepository = carServiceRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }


    @Override
    public List<RepairListDto> getAllServicesByDeletedAtNull() {
        return this.carServiceRepository.findAllByDeletedAtNull().stream().map(p -> modelMapper.map(p, RepairListDto.class)).toList();
    }

    @Override
    public HashMap<String, String> addService(RepairDto dto) throws AccessDeniedException {
        userService.isUserLogIn();;
        HashMap<String, String> result = new HashMap<>();
        Optional<Repair> optService = carServiceRepository.findByName(dto.getName());
        if(optService.isPresent()){
            result.put("status", "error");
            result.put("message", "Услуга " + dto.getName() + " вече съществува!");
            return result;
        }
        carServiceRepository.save(modelMapper.map(dto, Repair.class));
        result.put("status", "success");
        result.put("message", "Успешно добавена услуга " + dto.getName());
        return result;
    }

    @Override
    public HashMap<String, String> deleteService(UUID id) throws AccessDeniedException {
        userService.isUserLogIn();
        if( !userService.isAdmin(userService.getLoggedInUser().get())){
            //todo -> get message from some settings for all access denied exceptions
            throw new AccessDeniedException("Нямате права да извършите тази операция!");
        }
        HashMap<String, String> result = new HashMap<>();
        Optional<Repair> service = carServiceRepository.findById(id);
        if(service.isPresent()){
            service.get().setDeletedAt(LocalDateTime.now());
            carServiceRepository.save(service.get());
            result.put("status", "success");
            result.put("message", "Успешно изтрита услуга " + service.get().getName());
            return result;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Услуга с #" + id + " не съществува!");
    }

    @Override
    public HashMap<String, String> editService(EditRepairDto dto) throws AccessDeniedException {
        userService.isUserLogIn();;
        HashMap<String, String> result = new HashMap<>();
        Optional<Repair> optService = carServiceRepository.findById(dto.getId());
        if(!optService.isPresent()){
            result.put("status", "error");
            result.put("message", "Услуга #" + dto.getId() + " не съществува!");
            return result;
        }
        optService.get().setName(dto.getName());
        if(dto.getPrice() != null){
            optService.get().setPrice(dto.getPrice());
        }else{
            optService.get().setPrice(0);
        }
        carServiceRepository.save(optService.get());
        result.put("status", "success");
        result.put("message", "Успешно редактирана услуга " + dto.getName());
        return result;
    }

    @Override
    public EditRepairDto findById(UUID id) {
        Optional<Repair> service = carServiceRepository.findById(id);
        if(service.isPresent()){

            return modelMapper.map(service, EditRepairDto.class);
        }
        throw new RuntimeException("Няма намерена услуга с #" + id);
    }
}
