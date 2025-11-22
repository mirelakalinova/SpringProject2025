package com.example.mkalinova.app.company.service;



import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.company.data.dto.*;
import com.example.mkalinova.app.company.data.entity.Company;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface CompanyService {

    List<CompanyListDto> getAllActiveCompanies();

    List<Company> allCompaniesWithoutClient();

    HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto) throws AccessDeniedException;

    Optional<Company> findCompany(String name);


    boolean findByCompanyNameOrUic(String name, String uic);

    HashMap<String, String> deleteCompany(CompanyListDto company) throws AccessDeniedException;

//    <T> List<T> getAll(Class<T> dtoClass);

    List<CompanyRepairDto> findByClientId(Long id);

    <T> Object getById(Long id, Class<T> clazz);

    HashMap<String, String> updateCompany(EditCompanyDto editCompanyDto, boolean isClientPresent, Long clientId) throws AccessDeniedException;

   <T> Object findById(Long companyId, Class<T> clazz);

    List<Company> getAllCompaniesByClientId(Long id);

    ClientDto getCompanyClient(Long id);
    HashMap<String, String> removeClient(Long id, Long companyId) throws AccessDeniedException;

    List<FetchCompaniesDto> fetchCompaniesByClientId(Long id);

}
