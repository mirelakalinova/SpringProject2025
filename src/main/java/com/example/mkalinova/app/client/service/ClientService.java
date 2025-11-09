package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.dto.ClientRepairDto;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ClientService {

    <T> Object getById(Long id, Class<T> tClass);

    String addClientWithAdditionData(@Valid AddClientDto addClientDto,
                                     @Valid AddCarDto addCarDto,
                                     @Valid AddCompanyDto addCompanyDto,
                                     boolean companyIsFill);

    List<Client> getAll();

    void deleteClient(Long id);

    EditClientDto editClient(Long id);

    String updateClient(Long id, @Valid EditClientDto editClientDto);

    <T> List<T> findAll(Class<T> dtoClass);

    List<ClientRepairDto> findById(Long id);
}
