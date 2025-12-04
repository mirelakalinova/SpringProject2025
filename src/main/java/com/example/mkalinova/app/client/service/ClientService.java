package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.dto.ClientListDto;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.data.dto.FetchClientListDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import jakarta.validation.Valid;

import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {

   Optional<Client> getById(UUID id);

    HashMap<String, String> addClientWithAdditionalData(@Valid AddClientDto addClientDto,
                                                @Valid AddCarDto addCarDto,
                                                @Valid AddCompanyDto addCompanyDto,
                                                boolean companyIsFill) throws AccessDeniedException;

    List<ClientListDto> getAllWithCarsAndCompanies();

    void deleteClient(UUID id) throws AccessDeniedException;

    <T> T findClientById(UUID id, Class<T> clazz);

    HashMap<String, String> updateClient(UUID id, @Valid EditClientDto editClientDto) throws AccessDeniedException;

    <T> List<T> findAll(Class<T> dtoClass);

    List<CarDto> getCarsByClient(UUID id);

    HashMap<String, String> removeCar(UUID id, UUID clientId) throws AccessDeniedException;

    HashMap<String, String> removeCompany(UUID id, UUID clientId) throws AccessDeniedException;

    List<FetchClientListDto> fetchAllClientsByDeletedAtNull();
}
