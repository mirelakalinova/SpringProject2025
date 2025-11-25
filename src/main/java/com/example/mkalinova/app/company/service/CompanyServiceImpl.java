package com.example.mkalinova.app.company.service;

import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.*;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ClientRepository clientRepository;


    public CompanyServiceImpl(CompanyRepository companyRepository, ModelMapper mapper, UserService userService, ClientRepository clientRepository) {
        this.companyRepository = companyRepository;
        this.modelMapper = mapper;

        this.userService = userService;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<CompanyListDto> getAllActiveCompanies() {

        return companyRepository.findAllByDeletedAtNull().stream().map(c -> modelMapper.map(c, CompanyListDto.class)).toList();
    }

    @Override
    public List<Company> allCompaniesWithoutClient() {
        return companyRepository.findByClientIsNull();
    }

    @Override
    public HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto) throws AccessDeniedException {
        userService.isUserLogIn();
        HashMap<String, String> result = new HashMap<>();
        if (companyRepository.findByName(addCompanyDto.getName()).isPresent()) {
            result.put("status", "error");
            result.put("message", "Компания с име: " + addCompanyDto.getName() + " вече съществува!");
            return result;
        }

        try {
            Company company = modelMapper.map(addCompanyDto, Company.class);
            companyRepository.save(company);
            result.put("status", "success");
            result.put("Message", "Успешно добавена компания: " + addCompanyDto.getName());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Нещо се обърка при добавянето на компания!");
        }

    }

    @Override
    public Optional<Company> findCompany(String name) {
        return companyRepository.findByName(name);


    }


    @Override
    public boolean findByCompanyNameOrUic(String name, String uic) {
        return companyRepository.findByUic(uic).isPresent() || companyRepository.findByName(name).isPresent();
    }

    @Override
    public HashMap<String, String> deleteCompany(String id) throws AccessDeniedException {
        UUID uuid = UUID.fromString(id);
        Optional<User> user = userService.getLoggedInUser();
        if (user.isEmpty() || !userService.isAdmin(user.get())) {
            throw new AccessDeniedException("Нямате права да извършите тази операция!");

        }


        Optional<Company> companyToDelete = companyRepository.findById(uuid);

        HashMap<String, String> result = new HashMap<>();
        if (companyToDelete.isPresent()) {

            companyToDelete.get().setDeleteAd(LocalDateTime.now());
            companyRepository.saveAndFlush(companyToDelete.get());
            result.put("status", "success");
            result.put("message", "Успешно изтрита фирма с ЕИК: " + companyToDelete.get().getUic());

            return result;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма намерена фирма с #" + uuid);
        }

    }

    @Override
    public List<CompanyRepairDto> findByClientId(UUID id) {

        List<CompanyRepairDto> companylist = new ArrayList<>();
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {

            if (Objects.equals(company.getClient().getId(), id)) {

                companylist.add(modelMapper.map(company, CompanyRepairDto.class));
            }
        }

        return companylist;


    }

    @Override
    public <T> Object getById(UUID id, Class<T> clazz) {
        if (companyRepository.findById(id).isPresent()) {
            return modelMapper.map(companyRepository.findById(id).get(), clazz);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма фирма с подаденото #" + id);
        }
    }

    @Override
    public HashMap<String, String> updateCompany(EditCompanyDto editCompanyDto, boolean isClientPresent, UUID clientId) throws AccessDeniedException {

        userService.isUserLogIn();

        Optional<Company> optCompany = companyRepository.findById(editCompanyDto.getId());
        if (!optCompany.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма намерена фирма с #:" + editCompanyDto.getId());
        }
        HashMap<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        optCompany.get().setName(editCompanyDto.getName());
        optCompany.get().setUic(editCompanyDto.getUic());
        optCompany.get().setVatNumber(editCompanyDto.getVatNumber());
        optCompany.get().setAddress(editCompanyDto.getAddress());
        optCompany.get().setAccountablePerson(editCompanyDto.getAccountablePerson());
        sb.append("Успешно обновена фирма: ").append(editCompanyDto.getName());
        if (isClientPresent) {
            Optional<Client> client = clientRepository.findById(clientId);

            if (client.isPresent()) {
                if (optCompany.get().getClient() != null &&
                        optCompany.get().getClient().getId() != clientId) {
                    result.put("status", "error");
                    result.put("message", "Фирмата има вече има клиент!");

                    return result;
                } else if (optCompany.get().getClient() == null) {
                    optCompany.get().setClient(client.get());
                    sb.append("Успешно добавен клиент към ").append(editCompanyDto.getName());
                    result.put("status", "success");
                    result.put("message", sb.toString());

                }
            }
        }
        companyRepository.saveAndFlush(optCompany.get());
        result.put("status", "success");
        result.put("message", sb.toString());
        return result;
    }


    @Override
    public <T> Object findById(UUID companyId, Class<T> clazz) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company.<Object>map(value -> modelMapper.map(value, clazz)).orElse(null);


    }

    @Override
    public List<Company> getAllCompaniesByClientId(UUID id) {
        return companyRepository.findAllByClientId(id);
    }

    @Override
    public ClientDto getCompanyClient(UUID id) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            Client client = company.get().getClient();
            if (client == null) {
                return null;
            }
            return modelMapper.map(company.get().getClient(), ClientDto.class);
        }
        return null;
    }

    //todo -> add UTest
    @Override
    public HashMap<String, String> removeClient(UUID id, UUID companyId) throws AccessDeniedException {
        Optional<Company> companyToUpdate = companyRepository.findById(companyId);
        Optional<Client> clientToRemove = clientRepository.findById(id);
        HashMap<String, String> result = new HashMap<>();
        if (companyToUpdate.isPresent() && clientToRemove.isPresent()) {
            Client client = companyToUpdate.get().getClient();
            if (client != null && Objects.equals(client.getId(), clientToRemove.get().getId())) {
                companyToUpdate.get().setClient(null);
                companyRepository.saveAndFlush(companyToUpdate.get());
                result.put("status", "success");
                result.put("message", "Успешно премахнат клиент: " + clientToRemove.get().getName());
                return result;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Клиент с #" + id + " не беше намерен!");

            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Компания с #" + companyId + " не беше намерена!");
        }
    }

    @Override
    public List<FetchCompaniesDto> fetchCompaniesByClientId(UUID id) {
        List<Company> companies = companyRepository.findAllByClientIdAndDeletedAtNull(id);
        return companies.stream().map(c -> modelMapper.map(c, FetchCompaniesDto.class)).toList();
    }
}



