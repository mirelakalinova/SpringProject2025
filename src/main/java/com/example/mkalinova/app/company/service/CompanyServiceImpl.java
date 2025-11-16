package com.example.mkalinova.app.company.service;

import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.dto.CompanyRepairDto;
import com.example.mkalinova.app.company.data.dto.EditCompanyDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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
    public HashMap<String, String> deleteCompany(CompanyListDto company) throws AccessDeniedException {

        Optional<User> user = userService.getLoggedInUser();
        if (user.isEmpty() || !userService.isAdmin(user.get())) {
            throw new AccessDeniedException("Нямате права да извършите тази операция!");

        }


        Optional<Company> companyToDelete = companyRepository.findById(company.getId());

        HashMap<String, String> result = new HashMap<>();
        if (companyToDelete.isPresent()) {

            companyToDelete.get().setDeleteAd(LocalDateTime.now());
            companyRepository.saveAndFlush(companyToDelete.get());
            result.put("status", "success");
            result.put("message", "Успешно изтрита фирма с ЕИК: " + companyToDelete.get().getUic());

            return result;
        } else {
            throw new NullPointerException("Няма намерена фирма с това #" + company.getId());
        }

    }

//    @Override
//    public <T> List<T> getAll(Class<T> dtoClass) {
//        List<Company> companies =
//                companyRepository.findAll().stream()
//                        .sorted(Comparator.comparing(Company::getName))
//                        .toList();
//        List<T> dtoList = new ArrayList<>();
//        for (Company company : companies) {
//            T dto = modelMapper.map(company, dtoClass);
//            dtoList.add(dto);
//        }
//        return dtoList;
//    }

    @Override
    public List<CompanyRepairDto> findByClientId(Long id) {

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
    public <T> Object getById(Long id, Class<T> clazz) {
        if (companyRepository.findById(id).isPresent()) {
            return modelMapper.map(companyRepository.findById(id).get(), clazz);

        } else {
            throw new NullPointerException("Няма фирма с подаденото #" + id);
        }
    }

    @Override
    public HashMap<String, String> updateCompany(EditCompanyDto editCompanyDto, boolean isClientPresent, Long clientId) throws AccessDeniedException {

        userService.isUserLogIn();

        Optional<Company> optCompany = companyRepository.findById(editCompanyDto.getId());
        if (!optCompany.isPresent()) {
            throw new NullPointerException("Няма намерена фирма с #:" + editCompanyDto.getId());
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
                } else if(optCompany.get().getClient() == null){
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
    public <T> Object findById(Long companyId, Class<T> clazz) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company.<Object>map(value -> modelMapper.map(value, clazz)).orElse(null);


    }

    @Override
    public List<Company> getAllCompaniesByClientId(Long id) {
        return companyRepository.findAllByClientId(id);
    }

    @Override
    public ClientDto getCompanyClient(Long id) {
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
    public HashMap<String, String> removeClient(Long id, Long companyId) throws AccessDeniedException {
        Optional<Company> companyToUpdate = companyRepository.findById(companyId);
        Optional<Client> clientToRemove = clientRepository.findById(id);
        HashMap<String, String> result = new HashMap<>();
        if (companyToUpdate.isPresent() && clientToRemove.isPresent()) {
            Client client = companyToUpdate.get().getClient();
            if (Objects.equals(client.getId(), clientToRemove.get().getId())) {
                companyToUpdate.get().setClient(null);
                companyRepository.saveAndFlush(companyToUpdate.get());
                result.put("status", "success");
                result.put("message", "Успешно премахнат клиент: " + clientToRemove.get().getName());
                return result;
            }

            } else {
                throw new NullPointerException("Компания с #" + companyId + " не беше намерен!");
            }

        result.put("status", "error");
        result.put("message", "Нещо се обърка");
        return result;
    }
}



