package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.dto.ClientListDto;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.data.dto.FetchClientListDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyClientListDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class ClientServiceImpl implements ClientService {
	
	private final ClientRepository clientRepository;
	private final CarService carService;
	private final CarRepository carRepository;
	private final CompanyService companyService;
	private final CompanyRepository companyRepository;
	private final ModelMapper modelMapper;
	private final UserService userService;
	
	public ClientServiceImpl(ClientRepository clientRepository, CarService carService, CarRepository carRepository, CompanyService companyService, CompanyRepository companyRepository, ModelMapper modelMapper, UserService userService) {
		this.clientRepository = clientRepository;
		this.carService = carService;
		this.carRepository = carRepository;
		this.companyService = companyService;
		this.companyRepository = companyRepository;
		this.modelMapper = modelMapper;
		this.userService = userService;
	}
	
	
	@Override
	public Optional<Client> getById(UUID id) {
		
		return clientRepository.findById(id);
	}
	
	@Override
	@Transactional
	public HashMap<String, String> addClientWithAdditionalData(AddClientDto addClientDto, AddCarDto addCarDto, AddCompanyDto addCompanyDto, boolean companyIsFill) throws AccessDeniedException {
		log.debug("Attempt to add client with car and / or company");
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		Optional<Client> opt = clientRepository.findByPhone(addClientDto.getPhone());
		
		if (opt.isPresent()) {
			result.put("status", "error");
			result.put("message", "Клиент с тел. номер:" + addClientDto.getPhone() + " вече съществува!");
			log.warn("Return error message: the client's phone number is present {}", opt.get().getPhone());
			return result;
		}
		
		Client clientToAdd = modelMapper.map(addClientDto, Client.class);
		clientRepository.save(clientToAdd);
		
		boolean addCarDtoIsPresent = addCarDto != null && addCarDto.getRegistrationNumber() != null && !addCarDto.getRegistrationNumber().isEmpty();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Успешно добавен клиент: ").append(addClientDto.getFirstName()).append(" ").append(addClientDto.getLastName()).append(" с тел.: ").append(addClientDto.getPhone()).append(System.lineSeparator());
		
		if (addCarDtoIsPresent) {
			addCarDto.setClientId(clientToAdd.getId());
			HashMap<String, String> saveCarResult = carService.addCarAndReturnMessage(addCarDto);
			
			if (saveCarResult.get("status").equals("error")) {
				result.put("status", saveCarResult.get("status"));
				result.put("message", saveCarResult.get("message"));
				return result;
			} else {
				sb.append(saveCarResult.get("message")).append(System.lineSeparator());
			}
		}
		
		if (companyIsFill) {
			addCompanyDto.setClientId(clientToAdd.getId());
			HashMap<String, String> saveCompanyResult = companyService.saveCompany(addCompanyDto);
			if (saveCompanyResult.get("status").equals("success")) {
				sb.append("Успешно закачена фирма: ").append(addCompanyDto.getName()).append(" с ЕИК: ").append(addCompanyDto.getUic()).append(" към клиент ").append(addClientDto.getFirstName()).append(" ").append(addClientDto.getLastName()).append(System.lineSeparator());
			} else {
				result.put("status", "error");
				result.put("message", "Фирма с име: " + addCompanyDto.getName() + " или ЕИК:" + addCompanyDto.getUic() + " вече принадлижи на клиент!");
				return result;
			}
			
		}
		result.put("status", "success");
		result.put("message", sb.toString());
		
		log.info("Successfully added client with additional data.. Client's phone number: {}", addClientDto.getPhone());
		return result;
		
	}
	
	
	@Override
	public List<ClientListDto> getAllWithCarsAndCompanies() {
		log.debug("Attempt to get all clients with cars and companies..");
		List<ClientListDto> clientList =
				
				this.clientRepository.findAllByDeletedAtNull().stream().map(c -> modelMapper.map(c, ClientListDto.class)).toList();
		clientList.forEach(c -> {
			List<Car> carlist = carService.getAllCarByClientId(c.getId());
			List<Company> companyList = companyService.getAllCompaniesByClientId(c.getId());
			companyList.forEach(company -> {
				c.getCompanies().add(modelMapper.map(company, CompanyClientListDto.class));
				
			});
		});
		log.info("Successfully get all clients with cars and companies..");
		return clientList;
	}
	
	@Override
	@Transactional
	public void deleteClient(UUID id) throws AccessDeniedException {
		log.debug("Attempt to delete client with id {}", id);
		userService.isUserLogIn();
		Optional<User> user = userService.getLoggedInUser();
		if (user.isPresent()) {
			if (!userService.isAdmin(user.get())) {
				
				throw new AccessDeniedException("Нямате права да изтривате!");
			}
		}
		Optional<Client> client = clientRepository.findById(id);
		if (client.isPresent()) {
			List<Car> cars = carService.getAllCarByClientId(id);
			if (!cars.isEmpty()) {
				cars.forEach(c -> {
					c.setDeletedAt(LocalDateTime.now());
					carRepository.save(c);
					log.info("Successfully deleted car with registration number {}", c.getRegistrationNumber());
				});
				
			}
			List<Company> companies = companyService.getAllCompaniesByClientId(id);
			if (!companies.isEmpty()) {
				companies.forEach(c -> {
					c.setDeleteAd(LocalDateTime.now());
					companyRepository.save(c);
				});
			}
			client.get().setDeletedAt(LocalDateTime.now());
			clientRepository.save(client.get());
			log.info("Successfully deleted client with phone number {}", client.get().getPhone());
		} else {
			throw new NoSuchResourceException("Клиент с #" + id + " не съществува!");
		}
		
	}
	
	@Override
	public <T> T findClientById(UUID id, Class<T> clazz) {
		log.debug("Attempt to find client by id {}", id);
		Optional<Client> client = clientRepository.findById(id);
		if (client.isPresent()) {
			log.info("Successfully found client with id {}", id);
			return modelMapper.map(client, clazz);
		}
		throw new NoSuchResourceException("Няма намерен клиент с #" + id);
	}
	
	@Override
	@Transactional
	public HashMap<String, String> updateClient(UUID id, EditClientDto editClientDto) throws AccessDeniedException {
		log.debug("Attempt to update client by id {}", id);
		userService.isUserLogIn();
		HashMap<String, String> result = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		Optional<Client> client = clientRepository.findById(editClientDto.getId());
		if (client.isEmpty()) {
			throw new NoSuchResourceException("Няма намерен клиентс с #" + editClientDto.getId());
		}
		Optional<Client> clientByPhone = clientRepository.findByPhone(editClientDto.getPhone());
		
		if (clientByPhone.isPresent()) {
			if (!clientByPhone.get().getId().equals(client.get().getId())) {
				log.warn("Return error message: Client with phone number {} is present in other client", editClientDto.getPhone());
				result.put("status", "error");
				result.put("message", "Клиент с телефон: " + editClientDto.getPhone() + " вече съществува!");
				return result;
			}
		}
		sb.append("Успешно обновен клиент").append(System.lineSeparator());
		UUID carId = editClientDto.getCarId();
		if (carId != null) {
			Car car = (Car) carService.findById(carId, Car.class);
			if (car.getClient() == null) {
				car.setClient(client.get());
				carRepository.save(car);
				
				sb.append("Автомобил: ").append(car.getRegistrationNumber()).append(" беше добавен успешно към клиента!").append(System.lineSeparator());
				log.info("Successfully added car with registration number {} to client with id {}", car.getRegistrationNumber(), id);
			} else {
				result.put("status", "error");
				result.put("message", "Автомобил: " + car.getRegistrationNumber() + " вече принадлежи на друг клиент!");
				log.warn("Return error message: Car with registration number {} is present in other client relation", car.getRegistrationNumber());
				return result;
			}
		}
		UUID companyId = editClientDto.getCompanyId();
		if (companyId != null) {
			Company company = (Company) companyService.findById(companyId, Company.class);
			if (company.getClient() == null) {
				company.setClient(client.get());
				companyRepository.save(company);
				
				sb.append("Фирма: ").append(company.getName()).append(" беше добавена успешно към клиента!").append(System.lineSeparator());
				log.info("Successfully added company with name {} to client with id {}", company.getName(), id);
			} else {
				result.put("status", "error");
				result.put("message", "Фирма: " + company.getName() + " вече принадлежи на друг клиент!");
				log.warn("Return error message: Company with name {} is present in other client relation", company.getName());
				return result;
			}
		}
		
		
		result.put("status", "success");
		result.put("message", sb.toString());
		clientRepository.save(client.get());
		log.info("Successfully updated client with id {}", id);
		return result;
	}
	
	@Override
	public <T> List<T> findAll(Class<T> dtoClass) {
		log.debug("Attempt to find all clients..");
		List<Client> clientList = this.clientRepository.findAllByDeletedAtNull();
		List<T> dtoClientList = new ArrayList<>();
		clientList.forEach(c -> dtoClientList.add(modelMapper.map(c, dtoClass)));
		log.info("Successfully found all clients..");
		
		return dtoClientList;
	}
	
	@Override
	public List<CarDto> getCarsByClient(UUID id) {
		log.debug("Attempt to find all cars by client id {}", id);
		Optional<Client> client = clientRepository.findById(id);
		if (client.isPresent()) {
			List<Car> cars = client.get().getCars();
			log.info("Successfully found all cars by client id {}", id);
			return cars.stream().map(c -> modelMapper.map(c, CarDto.class)).toList();
		}
		log.info("No cars found by client id {}", id);
		return Collections.emptyList();
	}
	
	@Override
	public HashMap<String, String> removeCar(UUID id, UUID clientId) throws AccessDeniedException {
		userService.isUserLogIn();
		log.debug("Attempt to remove car with id {} from client id {}", id, clientId);
		Optional<Car> car = carRepository.findById(id);
		Optional<Client> clientToUpdate = clientRepository.findById(clientId);
		HashMap<String, String> result = new HashMap<>();
		if (car.isPresent() && clientToUpdate.isPresent()) {
			Client client = car.get().getClient();
			if (Objects.equals(client.getId(), clientToUpdate.get().getId())) {
				car.get().setClient(null);
				carRepository.save(car.get());
				result.put("status", "success");
				result.put("message", "Успешно премахнат автомобил с рег. номер: " + car.get().getRegistrationNumber());
				log.info("Successfully removed car with id {} from  client id {}", id, clientId);
				return result;
			}
			
		} else {
			throw new NoSuchResourceException("Автомобил с #" + id + " не беше намерен!");
		}
		result.put("status", "error");
		result.put("message", "Нещо се обърка");
		log.warn("Unsuccessfully removed car with id {} from  client id {}", id, clientId);
		return result;
	}
	
	@Override
	public HashMap<String, String> removeCompany(UUID id, UUID clientId) throws AccessDeniedException {
		userService.isUserLogIn();
		log.debug("Attempt to remove company with id {} from client id {}", id, clientId);
		Optional<Company> company = companyRepository.findById(id);
		Optional<Client> clientToUpdate = clientRepository.findById(clientId);
		HashMap<String, String> result = new HashMap<>();
		if (company.isPresent() && clientToUpdate.isPresent()) {
			Client client = company.get().getClient();
			if (Objects.equals(client.getId(), clientToUpdate.get().getId())) {
				company.get().setClient(null);
				companyRepository.save(company.get());
				result.put("status", "success");
				result.put("message", "Успешно премахната фирма с ЕИК: " + company.get().getUic());
				log.info("Successfully removed company with id {} from  client id {}", id, clientId);
				return result;
			}
			
		} else {
			throw new NoSuchResourceException("Фирма с #:" + id + " не беше намерена!");
		}
		
		result.put("status", "error");
		result.put("message", "Нещо се обърка");
		log.warn("Unsuccessfully removed company with id {} from  client id {}", id, clientId);
		return result;
	}
	
	@Override
	public List<FetchClientListDto> fetchAllClientsByDeletedAtNull() {
		log.debug("Attempt to fetch all clients..");
		List<FetchClientListDto> list = clientRepository.findAllByDeletedAtNull().stream().map(c -> modelMapper.map(c, FetchClientListDto.class)).toList();
		log.info("Successfully fetched all clients..");
		return list;
	}
}
