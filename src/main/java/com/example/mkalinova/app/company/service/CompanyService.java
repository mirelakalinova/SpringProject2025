package com.example.mkalinova.app.company.service;



import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyRepairDto;
import com.example.mkalinova.app.company.data.entity.Company;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface CompanyService {

    List<Company> getAllCompanies();

    List<Company> allCompaniesWithoutClient();

    HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto);

    Optional<Company> findCompany(String name);


    boolean findByCompanyNameOrUic(String name, int uic);

    String deleteCompany(Company company);

    <T> List<T> getAll(Class<T> dtoClass);

    List<CompanyRepairDto> findByClientId(Long id);

    Company getById(Long id);

    HashMap<String, String> updateCompany(Long id, EditClientDto editClientDto) throws AccessDeniedException;

   <T> Object findById(Long companyId, Class<T> clazz);

    List<Company> getAllCompaniesByClientId(Long id);
}
