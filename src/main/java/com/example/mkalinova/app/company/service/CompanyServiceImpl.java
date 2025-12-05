package com.example.mkalinova.app.company.service;

import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.dto.EditCompanyDto;
import com.example.mkalinova.app.company.data.dto.FetchCompaniesDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {
	private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);
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
		log.debug("Attempt to fetch all companies..");
		return companyRepository.findAllByDeletedAtNull().stream().map(c -> modelMapper.map(c, CompanyListDto.class)).toList();
	}
	
	@Override
	public List<Company> allCompaniesWithoutClient() {
		log.debug("Attempt to fetch all companies without a client..");
		return companyRepository.findByClientIsNull();
	}
	
	@Override
	@Transactional
	public HashMap<String, String> saveCompany(AddCompanyDto addCompanyDto) throws AccessDeniedException {
		log.debug("Attempt to add company with uic {}", addCompanyDto.getUic());
		
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		if (companyRepository.findByName(addCompanyDto.getName()).isPresent()) {
			result.put("status", "error");
			result.put("message", "Компания с име: " + addCompanyDto.getName() + " вече съществува!");
			log.warn("Return error message: the company's name is present {}", addCompanyDto.getName());
			return result;
		}
		
		try {
			Company company = modelMapper.map(addCompanyDto, Company.class);
			if (addCompanyDto.getClientId() != null) {
				Optional<Client> client = clientRepository.findById(addCompanyDto.getClientId());
				if (client.isPresent()) {
					company.setClient(client.get());
				} else {
					result.put("status", "error");
					result.put("message", "Нещо се обърка при добавяне на компания и закаченето ѝ към клиент! ");
					log.error("Unsuccessfully add new company with uic number: {}. The client with id {} does not exist!", addCompanyDto.getUic(), addCompanyDto.getClientId());
					return result;
				}
			}
			companyRepository.save(company);
			result.put("status", "success");
			result.put("message", "Успешно добавена компания: " + addCompanyDto.getName());
			log.info("Successfully added company with name: {}", addCompanyDto.getName());
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Нещо се обърка при добавянето на компания!");
		}
		
	}
	
	@Override
	public Optional<Company> findCompanyByNameOrUic(String name, String uic) {
		log.info("Attempt to find company by name {}", name);
		Optional<Company> companyByName = companyRepository.findByName(name);
		if (companyByName.isPresent()) {
			log.info("Successfully find company name {}", name);
			return companyByName;
		}
		Optional<Company> companyByUic = companyRepository.findByUic(uic);
		if (companyByUic.isPresent()) {
			log.info("Successfully find company uic {}", uic);
			return companyByUic;
		}
		log.info("No company with name {} and uic {} is present", name, uic);
		
		return Optional.empty();
		
		
	}
	
	@Override
	public HashMap<String, String> deleteCompany(String id) throws AccessDeniedException {
		UUID uuid = UUID.fromString(id);
		log.debug("Attempt to delete company by id {}", id);
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
			log.info("Successfully deleted company with uic: {}", companyToDelete.get().getUic());
			
			return result;
		} else {
			throw new NoSuchResourceException( "Няма намерена фирма с #" + uuid);
		}
		
	}
	
	
	@Override
	public <T> Object getById(UUID id, Class<T> clazz) {
		if (companyRepository.findById(id).isPresent()) {
			return modelMapper.map(companyRepository.findById(id).get(), clazz);
			
		} else {
			throw new NoSuchResourceException( "Няма фирма с подаденото #" + id);
		}
	}
	
	@Override
	public HashMap<String, String> updateCompany(EditCompanyDto editCompanyDto, boolean isClientPresent, UUID clientId) throws AccessDeniedException {
		log.debug("Attempt to update a company with id {}", editCompanyDto.getId());
		userService.isUserLogIn();
		
		Optional<Company> optCompany = companyRepository.findById(editCompanyDto.getId());
		if (optCompany.isEmpty()) {
			throw new NoSuchResourceException( "Няма намерена фирма с #:" + editCompanyDto.getId());
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
					log.warn("Returned error message : client with id {} is present in another relation", client.get().getId());
					return result;
				} else if (optCompany.get().getClient() == null) {
					optCompany.get().setClient(client.get());
					sb.append("Успешно добавен клиент към ").append(editCompanyDto.getName());
					result.put("status", "success");
					result.put("message", sb.toString());
					log.info("Successfully added client with id {} to company with id {}", client.get().getId(), editCompanyDto.getId());
				}
			}
		}
		companyRepository.saveAndFlush(optCompany.get());
		result.put("status", "success");
		result.put("message", sb.toString());
		log.info("Successfully updated company with id {}", editCompanyDto.getId());
		return result;
	}
	
	
	@Override
	public <T> Object findById(UUID companyId, Class<T> clazz) {
		log.debug("Attempt to find company by id {}", companyId);
		Optional<Company> company = companyRepository.findById(companyId);
		return company.<Object>map(value -> modelMapper.map(value, clazz)).orElse(null);
		
		
	}
	
	@Override
	public List<Company> getAllCompaniesByClientId(UUID id) {
		log.debug("Attempt to find all companies by client id {}", id);
		return companyRepository.findAllByClientId(id);
	}
	
	@Override
	public ClientDto getCompanyClient(UUID id) {
		log.debug("Attempt to find client of a company with id {}", id);
		Optional<Company> company = companyRepository.findById(id);
		if (company.isPresent()) {
			Client client = company.get().getClient();
			if (client == null) {
				return null;
			}
			log.info("Successfully found client of a company with id {}", id);
			return modelMapper.map(company.get().getClient(), ClientDto.class);
		}
		return null;
	}
	
	@Override
	public HashMap<String, String> removeClient(UUID id, UUID companyId) {
		log.debug("Attempt to remove client with id {} of a company with id {}", id, companyId);
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
				log.info("Successfully removed client with id {} of a company with id {}", id, companyId);
				return result;
			} else {
				throw new NoSuchResourceException( "Клиент с #" + id + " не беше намерен!");
			}
		} else {
			throw new NoSuchResourceException( "Компания с #" + companyId + " не беше намерена!");
		}
	}
	
	@Override
	public List<FetchCompaniesDto> fetchCompaniesByClientId(UUID id) {
		log.debug("Attempt to fetch all companies by client id {}", id);
		List<Company> companies = companyRepository.findAllByClientIdAndDeletedAtNull(id);
		return companies.stream().map(c -> modelMapper.map(c, FetchCompaniesDto.class)).toList();
	}
}



