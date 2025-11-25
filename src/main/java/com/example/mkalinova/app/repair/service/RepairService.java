package com.example.mkalinova.app.repair.service;

import com.example.mkalinova.app.repair.data.dto.RepairDto;
import com.example.mkalinova.app.repair.data.dto.EditRepairDto;
import com.example.mkalinova.app.repair.data.dto.RepairListDto;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface RepairService {

     List<RepairListDto> getAllServicesByDeletedAtNull();
     HashMap<String, String> addService(RepairDto dto) throws AccessDeniedException;
     HashMap<String, String> deleteService(UUID id) throws AccessDeniedException;
     HashMap<String, String> editService(EditRepairDto dto) throws AccessDeniedException;

    EditRepairDto findById(UUID id);
}
