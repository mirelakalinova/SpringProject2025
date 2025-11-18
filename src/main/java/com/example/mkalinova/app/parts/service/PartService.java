package com.example.mkalinova.app.parts.service;

import com.example.mkalinova.app.parts.data.dto.EditPartDto;
import com.example.mkalinova.app.parts.data.dto.PartDto;
import com.example.mkalinova.app.parts.data.dto.PartListDto;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;

public interface PartService {

     List<PartListDto> getAllPartsByDeletedAtNull();
     HashMap<String, String> addPart(PartDto partDto) throws AccessDeniedException;
     HashMap<String, String> deletePart(Long id) throws AccessDeniedException;
     HashMap<String, String> editPart(EditPartDto partDto) throws AccessDeniedException;

    EditPartDto findById(Long id);
}
