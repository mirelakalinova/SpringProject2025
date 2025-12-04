package com.example.mkalinova.app.company.service;


import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.dto.EditCompanyDto;
import com.example.mkalinova.app.company.data.dto.FetchCompaniesDto;
import com.example.mkalinova.app.company.data.entity.Company;

import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {
	
	List<CompanyListDto> getAllActiveCompanies();
	
	List<Company> allCompaniesWithoutClient();
	
	HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto) throws AccessDeniedException;
	
	Optional<Company> findCompanyByNameOrUic(String name, String uic);
	
	
	HashMap<String, String> deleteCompany(String id) throws AccessDeniedException;
	
	
	<T> Object getById(UUID id, Class<T> clazz);
	
	HashMap<String, String> updateCompany(EditCompanyDto editCompanyDto, boolean isClientPresent, UUID clientId) throws AccessDeniedException;
	
	<T> Object findById(UUID companyId, Class<T> clazz);
	
	List<Company> getAllCompaniesByClientId(UUID id);
	
	ClientDto getCompanyClient(UUID id);
	
	HashMap<String, String> removeClient(UUID id, UUID companyId);
	
	List<FetchCompaniesDto> fetchCompaniesByClientId(UUID id);
	
}
