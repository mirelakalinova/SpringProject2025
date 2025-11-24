package com.example.mkalinova.app.client.service;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.dto.*;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyClientListDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

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
    public Optional<Client>  getById(Long id) {

        return clientRepository.findById(id);
    }

    @Override
    @Transactional
    public HashMap<String, String> addClientWithAdditionalData(AddClientDto addClientDto,
                                                               AddCarDto addCarDto,
                                                               AddCompanyDto addCompanyDto,
                                                               boolean companyIsFill) throws AccessDeniedException {

        userService.isUserLogIn();
        System.out.println(">>> addClientDto: " + addClientDto);
        System.out.println(">>> addCarDto: " + addCarDto);
        System.out.println(">>> addCompanyDto: " + addCompanyDto);
        HashMap<String, String> result = new HashMap<>();
        //1. Да проверим клиента
        Optional<Client> opt = clientRepository.findByPhone(addClientDto.getPhone());

        if (opt.isPresent()) {
            result.put("status", "error");
            result.put("message", "Клиент с тел. номер:" + addClientDto.getPhone() + " вече съществува!");
            return result;
        }

        Client clientToAdd;
        Optional<Car> car = Optional.empty();
        boolean addCarDtoIsPresent = addCarDto != null
                && addCarDto.getRegistrationNumber() != null
                && !addCarDto.getRegistrationNumber().isEmpty();
        ;
        clientToAdd = modelMapper.map(addClientDto, Client.class);

        //2. Да проверим колата
        if (addCarDtoIsPresent) {

            car = carService.findCar(addCarDto.getRegistrationNumber());
            if (car.isPresent()) {
                if (car.get().getClient() != null) {
                    result.put("status", "error");
                    result.put("message", "Кола с регистрационен номер: " + addCarDto.getRegistrationNumber() + " принадлежи вече на клиент!");
                    return result;
                }
            }
        }
        Optional<Company> company = Optional.empty();
        //3. Да проверим компанията, ако не е null
        if (companyIsFill) {
            company = companyService.findCompany(addCompanyDto.getName());
            if (company.isPresent()) {
                if (company.get().getClient() != null) {
                    result.put("status", "error");
                    result.put("message", "Фирма с ЕИК:" + addCompanyDto.getUic() + " вече принадлижи на клиент!");
                    return result;
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Успешно добавен клиент: ")
                .append(addClientDto.getFirstName()).append(" ")
                .append(addClientDto.getLastName()).append(" с тел.: ").append(addClientDto.getPhone())
                .append(System.lineSeparator());
        boolean isCarSaved = false;
        boolean isCompanySaved = false;
        clientRepository.save(clientToAdd);
        if (addCarDtoIsPresent) {
            if (car.isEmpty()) {
                HashMap<String, String> saveCarResult = carService.addCarAndReturnMessage(addCarDto);
                sb.append(saveCarResult.get("message")).append(System.lineSeparator());
                if (saveCarResult.get("status").equals("success")) {
                    isCarSaved = true;
                }
            } else {
                car.get().setClient(clientToAdd);
//                carRepository.save(car.get());
                isCarSaved = true;

            }

        }
        if (companyIsFill) {
            if (company.isEmpty()) {
                HashMap<String, String> saveCompanyResult = companyService.saveCompany(addCompanyDto);
                sb.append(saveCompanyResult.get("message")).append(System.lineSeparator());
                if (saveCompanyResult.get("status").equals("success")) {
                    isCompanySaved = true;

                }
            } else {

                isCompanySaved = true;


            }
        }

        if (companyIsFill) {
            Optional<Company> newCompany = companyRepository.findByName(addCompanyDto.getName());
            if (newCompany.isPresent()) {
                newCompany.get().setClient(clientToAdd);
                companyRepository.save(newCompany.get());
                sb.append("Успешно закачена фирма: ").append(addCompanyDto.getName()).append(" с ЕИК: ")
                        .append(addCompanyDto.getUic())
                        .append(" към клиент ").append(addClientDto.getFirstName())
                        .append(" ")
                        .append(addClientDto.getLastName()).append(System.lineSeparator());
            } else {
                throw new RuntimeException();
            }
        }

        if (addCarDtoIsPresent) {
            Optional<Car> newCar = carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber());
            if (newCar.isPresent()) {
                newCar.get().setClient(clientToAdd);
                //  carRepository.save(newCar.get());
                sb.append("Успешно закачена кола: ").append(addCarDto.getRegistrationNumber())
                        .append(" към клиент ").append(addClientDto.getFirstName())
                        .append(" ")
                        .append(addClientDto.getLastName()).append(System.lineSeparator());

            } else {
                throw new RuntimeException("Нещо се обърка при добавянето на автомобил");
            }
        }

        result.put("status", "success");
        result.put("message", sb.toString());

        return result;

    }


    @Override
    public List<ClientListDto> getAllWithCarsAndCompanies() {

        List<ClientListDto> clientList =

         this.clientRepository.findAllByDeleteAdNull().stream()
                 .map(c->modelMapper.map(c, ClientListDto.class)).toList();
        clientList.forEach(c->{
            List<Car> carlist=  carService.getAllCarByClientId(c.getId());
            List<Company> companyList=  companyService.getAllCompaniesByClientId(c.getId());
            companyList.forEach(company->{
                c.getCompanies().add(modelMapper.map(company, CompanyClientListDto.class));

            });
        });
        return clientList;
    }

    @Override
    @Transactional
    public void deleteClient(Long id) throws AccessDeniedException {
        userService.isUserLogIn();
        Optional<User> user = userService.getLoggedInUser();
        if (user.isPresent()){
            if(!userService.isAdmin(user.get())){
                throw new AccessDeniedException("Нямате права да изтривате!");
            }
        }
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            //check cars
            List<Car> cars = carService.getAllCarByClientId(id);
            if (!cars.isEmpty()) {
                cars.forEach(c -> {
                    c.setDeletedAt(LocalDateTime.now());
                    carRepository.save(c);
                });

            }
            //check companies
            List<Company> companies = companyService.getAllCompaniesByClientId(id);
            if (!companies.isEmpty()) {
                companies.forEach(c ->
                {
                    c.setDeleteAd(LocalDateTime.now());
                    companyRepository.save(c);
                });
            }
            client.get().setDeleteAd(LocalDateTime.now());
            clientRepository.save(client.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Клиент с #" + id + " не съществува!");
        }

    }

    @Override
    public EditClientDto findClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            return modelMapper.map(client, EditClientDto.class);

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Няма намерен клиент с #" + id);
    }

    @Override
    @Transactional
    public HashMap<String, String> updateClient(Long id, EditClientDto editClientDto) throws AccessDeniedException {
        userService.isUserLogIn();
        HashMap<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        Optional<Client> client = clientRepository.findById(editClientDto.getId());
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Няма намерен клиентс с #" + editClientDto.getId());
        }
        sb.append("Успешно обновен клиент").append(System.lineSeparator());
        Long carId = editClientDto.getCarId();
        if (carId != null) {
            Car car = modelMapper.map(carService.findById(carId, Car.class), Car.class);
            if (car.getClient() == null) {
                car.setClient(client.get());
                carRepository.save(car);

                sb.append("Автомобил: ").append(car.getRegistrationNumber())
                        .append(" беше добавен успешно към клиента!")
                        .append(System.lineSeparator());

            } else {
                result.put("status", "error");
                result.put("message", "Автомобил: " + car.getRegistrationNumber() + " вече принадлежи на друг клиент!");
                return result;
            }
        }
        Long companyId = editClientDto.getCompanyId();
        if (companyId != null) {
            Company company =
                    modelMapper.map(companyService.findById(companyId, Company.class), Company.class);
            if (company.getClient() == null) {
                company.setClient(client.get());
                companyRepository.save(company);

                sb.append("Фирма: ").append(company.getName())
                        .append(" беше добавен успешно към клиента!")
                        .append(System.lineSeparator());

            } else {
                result.put("status", "error");
                result.put("message", "Фирма: " + company.getName() + " вече принадлежи на друг клиент!");
                return result;
            }
        }


        result.put("status", "success");
        result.put("message", sb.toString());
        clientRepository.save(client.get());
        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> dtoClass) {
        List<Client> clientList = this.clientRepository.findAllByDeleteAdNull();
        List<T> dtoClientList = new ArrayList<>();
        clientList.forEach(c -> dtoClientList.add(modelMapper.map(c, dtoClass)));

        return dtoClientList;
    }
// todo -> check is unnecessary
    @Override
    public List<ClientRepairDto> findById(Long id) {
        return List.of();
    }
    // todo -> check is unnecessary
    @Override
    public boolean findByPhone(String phoneNumber) {
        Optional<Client> client = clientRepository.findByPhone(phoneNumber);
        return client.isPresent();
    }

    @Override
    public List<CarDto> getCarsByClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            List<Car> cars = client.get().getCars();
            return cars.stream()
                    .map(c -> modelMapper.map(c, CarDto.class)).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public HashMap<String, String> removeCar(Long id, Long clientId) throws AccessDeniedException {
        userService.isUserLogIn();
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
                return result;
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Автомобил с #" + id + " не беше намерен!");
        }

        result.put("status", "error");
        result.put("message", "Нещо се обърка");
        return result;
    }

    @Override
    public HashMap<String, String> removeCompany(Long id, Long clientId) throws AccessDeniedException {
        userService.isUserLogIn();
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
                return result;
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Автомобил с #" + id + " не беше намерен!");
        }

        result.put("status", "error");
        result.put("message", "Нещо се обърка");
        return result;
    }

    @Override
    public List<FetchClientListDto> fetchAllClientsByDeletedAtNull() {
        List<FetchClientListDto> list =  clientRepository.findAllByDeleteAdNull().stream().map(c->modelMapper.map(c, FetchClientListDto.class)).toList();

        return list;
    }
}
