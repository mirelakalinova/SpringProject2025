package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.client.data.dto.*;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import jakarta.validation.Valid;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ClientService {

   Optional<Client> getById(Long id);

    HashMap<String, String> addClientWithAdditionalData(@Valid AddClientDto addClientDto,
                                                @Valid AddCarDto addCarDto,
                                                @Valid AddCompanyDto addCompanyDto,
                                                boolean companyIsFill) throws AccessDeniedException;

    List<ClientListDto> getAllWithCarsAndCompanies();

    void deleteClient(Long id) throws AccessDeniedException;

    EditClientDto findClientById(Long id);

    HashMap<String, String> updateClient(Long id, @Valid EditClientDto editClientDto) throws AccessDeniedException;

    <T> List<T> findAll(Class<T> dtoClass);

    List<ClientRepairDto> findById(Long id);
    boolean findByPhone(String phoneNumber);

    List<CarDto> getCarsByClient(Long id);

    HashMap<String, String> removeCar(Long id, Long clientId) throws AccessDeniedException;

    HashMap<String, String> removeCompany(Long id, Long clientId) throws AccessDeniedException;

    List<FetchClientListDto> fetchAllClientsByDeletedAtNull();
}
