package com.example.mkalinova.app.parts.service;


import com.example.mkalinova.app.parts.data.dto.EditPartDto;
import com.example.mkalinova.app.parts.data.dto.PartDto;
import com.example.mkalinova.app.parts.data.dto.PartListDto;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
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
        return this.partRepository.findAllByDeletedAtNull().stream().map(p -> modelMapper.map(p, PartListDto.class)).toList();
    }

    @Override
    public HashMap<String, String> addPart(PartDto partDto) throws AccessDeniedException {
        userService.isUserLogIn();;
        HashMap<String, String> result = new HashMap<>();
        Optional<Part> optPart = partRepository.findByName(partDto.getName());
        if(optPart.isPresent()){
            result.put("status", "error");
            result.put("message", "Част " + partDto.getName() + " вече съществува!");
            return result;
        }
        partRepository.save(modelMapper.map(partDto, Part.class));
        result.put("status", "success");
        result.put("message", "Успешно добавена част " + partDto.getName());
        return result;
    }

    @Override
    public HashMap<String, String> deletePart(UUID id) throws AccessDeniedException {
        userService.isUserLogIn();
        if( !userService.isAdmin(userService.getLoggedInUser().get())){
            //todo -> get message from some settings for all access denied exceptions
            throw new AccessDeniedException("Нямате права да извършите тази операция!");
        }
        HashMap<String, String> result = new HashMap<>();
        Optional<Part> part = partRepository.findById(id);
        if(part.isPresent()){
            part.get().setDeletedAt(LocalDateTime.now());
            partRepository.save(part.get());
            result.put("status", "success");
            result.put("message", "Успешно изтрита част " + part.get().getName());
            return result;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Част с #" + id + " не съществува!");
    }

    @Override
    public HashMap<String, String> editPart(EditPartDto partDto) throws AccessDeniedException {
        userService.isUserLogIn();;
        HashMap<String, String> result = new HashMap<>();
        Optional<Part> optPart = partRepository.findById(partDto.getId());
        if(optPart.isEmpty()){
            result.put("status", "error");
            result.put("message", "Част #" + partDto.getId() + " не съществува!");
            return result;
        }
        optPart.get().setName(partDto.getName());
        if(partDto.getPrice() != null){
            optPart.get().setPrice(partDto.getPrice());
        } else{
            optPart.get().setPrice(0);
        }
        partRepository.save(optPart.get());
        result.put("status", "success");
        result.put("message", "Успешно редактирана част " + partDto.getName());
        return result;
    }

    @Override
    public EditPartDto findById(UUID id) {
        Optional<Part> part = partRepository.findById(id);
        if(part.isPresent()){

            return modelMapper.map(part, EditPartDto.class);
        }
        throw new RuntimeException("Няма намерена част с #" + id);
    }
}
