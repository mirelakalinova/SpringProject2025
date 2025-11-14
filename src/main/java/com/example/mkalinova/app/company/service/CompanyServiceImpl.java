package com.example.mkalinova.app.company.service;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyRepairDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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
    public List<Company> getAllCompanies() {

        return companyRepository.findAll();
    }

    @Override
    public List<Company> allCompaniesWithoutClient() {
        return companyRepository.findByClientIsNull();
    }

    @Override
    public HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto) {
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
    public boolean findByCompanyNameOrUic(String name, int uic) {
        return companyRepository.findByUic(uic).isPresent() || companyRepository.findByName(name).isPresent();
    }

    @Override
    public String deleteCompany(Company company) {
        Optional<Company> companyToDelete = companyRepository.findById(company.getId());
        StringBuilder sb = new StringBuilder();
        if (companyToDelete.isPresent()) {
            sb.append(companyToDelete.get().getName()).append(" - ЕИК").append(companyToDelete.get().getUic());
            companyRepository.deleteById(companyToDelete.get().getId());
            return sb.toString();
        } else {
            return "Няма намерена фирма с това Id!";
        }

    }

    @Override
    public <T> List<T> getAll(Class<T> dtoClass) {
        List<Company> companies =
                companyRepository.findAll().stream()
                        .sorted(Comparator.comparing(Company::getName))
                        .toList();
        List<T> dtoList = new ArrayList<>();
        for (Company company : companies) {
            T dto = modelMapper.map(company, dtoClass);
            dtoList.add(dto);
        }
        return dtoList;
    }

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
    public Company getById(Long id) {
        if (companyRepository.findById(id).isPresent()) {
            return companyRepository.findById(id).get();

        } else {
            throw new NullPointerException("Няма фирма с подаденото #" + id);
        }
    }

    @Override
    @Transactional
    public HashMap<String, String> updateCompany(Long id, EditClientDto editClientDto) throws AccessDeniedException {

        return null;
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
}

