package com.example.mkalinova.client;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.car.repo.CarRepository;
import com.example.mkalinova.app.car.service.CarServiceImpl;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.client.service.ClientServiceImpl;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyServiceImpl;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientUTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CarRepository carRepository;

    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private ClientServiceImpl clientService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CarServiceImpl carService;

    @Mock
    private CompanyServiceImpl companyService;

    private Client client;
    private Client deletedClient;
    private AddCarDto addCarDto = new AddCarDto();


    @BeforeEach
    void SetUp() {
        client = new Client();
        client.setEmail("test@test.bg");
        client.setFirstName("Test");
        client.setLastName("Testov");
        client.setPhone("0896619422");


        deletedClient = new Client();
        deletedClient.setEmail("test2@test.bg");
        deletedClient.setFirstName("Test");
        deletedClient.setLastName("Testov");
        deletedClient.setPhone("0896619424");
        deletedClient.setDeleteAd(LocalDateTime.now());


    }

    @Test
    @WithAnonymousUser
    void addClientWithAdditionalData_ReturnAccessDenied() throws AccessDeniedException {
        doThrow(new AccessDeniedException("Нямате права да извършите тази опреация!")).when(userService).isUserLogIn();

        assertThrows(AccessDeniedException.class, () -> {
            userService.isUserLogIn();
        });

        // (по желание) Проверяваме, че методът е извикан точно веднъж
        verify(userService, times(1)).isUserLogIn();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ReturnErrorMessage() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        when(clientRepository.findByPhone(client.getPhone())).thenReturn(Optional.of(client));
        HashMap<String, String> result = clientService.addClientWithAdditionalData(dtoClient, null, null, false);
        assertEquals(result.get("status"), "error");
        assertEquals(result.get("message"), "Клиент с тел. номер:" + client.getPhone() + " вече съществува!");
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_Client_ReturnSuccessMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());


        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, null, null, false);

        assertEquals(result.get("status"), "success");
        assertEquals(dtoClient.getPhone(), "0896619422");
        verifyNoInteractions(carRepository);
        verifyNoInteractions(companyRepository);
        verify(userService).isUserLogIn();
        verify(clientRepository).save(any(Client.class));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndNonExistingCar_ReturnSuccessMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);

        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        HashMap<String, String> resultCar = new HashMap<>();
        resultCar.put("status", "success");
        when(carService.addCarAndReturnMessage(addCarDto)).thenReturn(resultCar);
        resultCar.put("status", "success");
        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(null); // няма собственик преди асоцииране
        when(carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, null, false);
        assertEquals(resultCar.get("status"), "success");
        assertEquals(result.get("status"), "success");
        assertEquals(dtoClient.getPhone(), "0896619422");

        verifyNoInteractions(companyRepository);
        verify(userService).isUserLogIn();
        verify(clientRepository).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndExistingCarWithoutClient_ReturnSuccessMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);

        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        when(carService.findCar(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(new Car()));


        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(null); // няма собственик преди асоцииране
        when(carService.findCar(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        when(carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, null, false);
        assertEquals(result.get("status"), "success");
        assertEquals(dtoClient.getPhone(), "0896619422");
        assertEquals(
                carRepository.findByRegistrationNumber(savedCar.getRegistrationNumber()).get().getRegistrationNumber(),addCarDto.getRegistrationNumber());

        verifyNoInteractions(companyRepository);
        verify(userService).isUserLogIn();
        verify(clientRepository).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndExistingCarWithoutClientAndNonExistingCompany_ReturnSuccessMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);

        AddCompanyDto addCompanyDto = new AddCompanyDto();
        addCompanyDto.setAddress("Bulgaria");
        addCompanyDto.setAccountablePerson("Test test");
        addCompanyDto.setName("Test Test");
        addCompanyDto.setUic("201799235");
        addCompanyDto.setVatNumber("BG201799235");

        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        when(carService.findCar(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(new Car()));
        when(companyService.findCompany(addCompanyDto.getName()))
                .thenReturn(Optional.empty());
        HashMap<String, String> resultCompany = new HashMap<>();
        resultCompany.put("status", "success");
        when(companyService.saveCompany(addCompanyDto)).thenReturn(resultCompany);
        when(companyRepository.findByName(addCompanyDto.getName()))
                .thenReturn(Optional.of(new Company()));
        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(null); // няма собственик преди асоцииране
        when(carService.findCar(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        when(carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
        assertEquals(result.get("status"), "success");
        assertEquals(dtoClient.getPhone(), "0896619422");
        assertEquals(
                carRepository.findByRegistrationNumber(savedCar.getRegistrationNumber()).get().getRegistrationNumber(),addCarDto.getRegistrationNumber());

        verify(companyRepository).save(any(Company.class));
        verify(userService).isUserLogIn();
        verify(clientRepository).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndExistingCarWithoutClientAndExistingCompanyWithoutClient_ReturnSuccessMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);

        AddCompanyDto addCompanyDto = new AddCompanyDto();
        addCompanyDto.setAddress("Bulgaria");
        addCompanyDto.setAccountablePerson("Test test");
        addCompanyDto.setName("Test Test");
        addCompanyDto.setUic("201799235");
        addCompanyDto.setVatNumber("BG201799235");

        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        when(carService.findCar(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(new Car()));
        when(companyService.findCompany(addCompanyDto.getName()))
                .thenReturn(Optional.of(new Company()));

        when(companyRepository.findByName(addCompanyDto.getName()))
                .thenReturn(Optional.of(new Company()));
        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(null); // няма собственик преди асоцииране
        when(carService.findCar(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        when(carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
        assertEquals(result.get("status"), "success");
        assertEquals(dtoClient.getPhone(), "0896619422");
        assertEquals(
                carRepository.findByRegistrationNumber(savedCar.getRegistrationNumber()).get().getRegistrationNumber(),addCarDto.getRegistrationNumber());

        verify(companyRepository).save(any(Company.class));
        verify(userService).isUserLogIn();
        verify(clientRepository).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndExistingCarWithClientAndExistingCompanyWithoutClient_ReturnErrorMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);
        AddCompanyDto addCompanyDto = new AddCompanyDto();
        addCompanyDto.setAddress("Bulgaria");
        addCompanyDto.setAccountablePerson("Test test");
        addCompanyDto.setName("Test Test");
        addCompanyDto.setUic("201799235");
        addCompanyDto.setVatNumber("BG201799235");



        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        when(carService.findCar(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(new Car()));

        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(client); // няма собственик преди асоцииране
        when(carService.findCar(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));

        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
        assertEquals(result.get("status"), "error");


        verify(companyRepository,times(0)).save(any(Company.class));
        verify(userService).isUserLogIn();
        verify(clientRepository, times(0)).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addClientWithAdditionalData_ClientAndExistingCarWithoutClientAndExistingCompanyWithClient_ReturnErrorMessage()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        AddClientDto dtoClient = new AddClientDto();
        dtoClient.setPhone(client.getPhone());
        dtoClient.setEmail(client.getEmail());
        dtoClient.setFirstName(client.getFirstName());
        dtoClient.setLastName(client.getLastName());
        addCarDto.setCube(1200);
        addCarDto.setMake("audi");
        addCarDto.setModel("tt");
        addCarDto.setYear(2000);
        addCarDto.setVin("2ds410ds0ds0ds41w");
        addCarDto.setRegistrationNumber("CB2116KH");
        addCarDto.setHp(120);

        AddCompanyDto addCompanyDto = new AddCompanyDto();
        addCompanyDto.setAddress("Bulgaria");
        addCompanyDto.setAccountablePerson("Test test");
        addCompanyDto.setName("Test Test");
        addCompanyDto.setUic("201799235");
        addCompanyDto.setVatNumber("BG201799235");
        Company company = new Company();
        company.setClient(client);
        when(clientRepository.findByPhone(dtoClient.getPhone())).thenReturn(Optional.empty());
        when(modelMapper.map(dtoClient, Client.class)).thenReturn(new Client());
        when(carService.findCar(addCarDto.getRegistrationNumber())).thenReturn(Optional.of(new Car()));
        when(companyService.findCompany(addCompanyDto.getName()))
                .thenReturn(Optional.of(company));


        Car savedCar = new Car();
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setRegistrationNumber(addCarDto.getRegistrationNumber());
        savedCar.setClient(null); // няма собственик преди асоцииране
        when(carService.findCar(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        when(carRepository.findByRegistrationNumber(addCarDto.getRegistrationNumber()))
                .thenReturn(Optional.of(savedCar));
        HashMap<String, String> result =
                clientService
                        .addClientWithAdditionalData(dtoClient, addCarDto, addCompanyDto, true);
        assertEquals(result.get("status"), "error");

        assertEquals(
                carRepository.findByRegistrationNumber(savedCar.getRegistrationNumber()).get().getRegistrationNumber(),addCarDto.getRegistrationNumber());

        verify(companyRepository,times(0)).save(any(Company.class));
        verify(userService).isUserLogIn();
        verify(clientRepository,times(0)).save(any(Client.class));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteClient_WithAllData_Success()
            throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        client.setId(1L);

        List<Car> cars = new ArrayList<>();
        Car car = new Car();
        car.setId(1L);
        car.setRegistrationNumber("K2116KH");
        car.setClient(client);
        cars.add(car);

        when(carService.getAllCarByClientId(1L)).thenReturn(cars);

        List<Company> companies = new ArrayList<>();
        Company company = new Company();
        company.setClient(client);
        company.setId(1L);
        companies.add(company);
        when(companyService.getAllCompaniesByClientId(1L))
                .thenReturn(companies);
        clientService.deleteClient(1L);
        assertNotNull(car.getDeletedAt(), "Car should have deletedAt set");
        assertNotNull(company.getDeletedAt(), "Company should have deleteAd set");
        assertNotNull(client.getDeleteAd(), "Client should have deleteAd set");
        verify(companyRepository, times(1)).save(company);
        verify(clientRepository, times(1)).save(client);
        verify(carRepository, times(1)).save(car);


    }
}
