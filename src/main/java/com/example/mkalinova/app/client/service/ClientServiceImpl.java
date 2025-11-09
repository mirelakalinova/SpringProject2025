package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.dto.ClientRepairDto;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public <T> Object getById(Long id, Class<T> tClass) {
        return null;
    }

    @Override
    public String addClientWithAdditionData(AddClientDto addClientDto, AddCarDto addCarDto, AddCompanyDto addCompanyDto, boolean companyIsFill) {
        return "";
    }

    @Override
    public List<Client> getAll() {
        return this.clientRepository.findAll();


    }

    @Override
    public void deleteClient(Long id) {

    }

    @Override
    public EditClientDto editClient(Long id) {
        return null;
    }

    @Override
    public String updateClient(Long id, EditClientDto editClientDto) {
        return "";
    }

    @Override
    public <T> List<T> findAll(Class<T> dtoClass) {
       List<Client> clientList = this.clientRepository.findAll();
       List<T> dtoClientList = new ArrayList<>();
       clientList.forEach(c-> dtoClientList.add(modelMapper.map(c, dtoClass)));
             

        return dtoClientList;
    }

    @Override
    public List<ClientRepairDto> findById(Long id) {
        return List.of();
    }
}
