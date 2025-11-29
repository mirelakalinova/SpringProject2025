package com.example.mkalinova.company;


import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.dto.EditCompanyDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import static org.junit.jupiter.api.Assertions.*;
import com.example.mkalinova.app.company.service.CompanyServiceImpl;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CompanyUTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CompanyServiceImpl service;


    private User admin;
    private User editor;
    private Company company;
    private Company companySecond;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        userRepository.deleteAll();
        admin = new User();
        admin.setFirstName("Mirela");
        admin.setLastName("Kalinova");
        admin.setUsername("admin");
        admin.setEmail("admin@test.bg");
        admin.setPassword("Password1234!");
        admin.setRole(UsersRole.ADMIN);

        editor = new User();
        editor.setFirstName("Mirela");
        editor.setLastName("Kalinova");
        editor.setUsername("editor");
        editor.setEmail("editor@test.bg");
        editor.setPassword("Password1234!");
        editor.setRole(UsersRole.EDITOR);

        Client client = new Client();
        client.setPhone("0896619422");
        client.setFirstName("Test");
        client.setLastName("Test");
        client.setEmail("Test@test.bg");
        clientRepository.save(client);
        userRepository.saveAndFlush(admin);
        company = new Company();
        company.setName("test");
        company.setId(UUID.randomUUID());
        company.setUic("201478523");
        company.setVatNumber("BG201478523");
        company.setAddress("Test address");
        company.setAccountablePerson("Test Test");
        companySecond = new Company();
        companySecond.setId(UUID.randomUUID());
        companySecond.setName("test2");
        companySecond.setUic("201478524");
        companySecond.setVatNumber("BG201478524");
        companySecond.setAddress("Test address");
        companySecond.setAccountablePerson("Test Test");
        companySecond.setDeleteAd(LocalDateTime.now());
        companySecond.setClient(client);
        companyRepository.save(company);
        companyRepository.save(companySecond);
    }

    @Test
    void getAllActiveCompanies_ReturnListOfOne(){
        when(companyRepository.findAllByDeletedAtNull())
                .thenReturn(List.of(company));

        List<CompanyListDto> result = service.getAllActiveCompanies();

        verify(companyRepository).findAllByDeletedAtNull();
        assertEquals(1, result.size());
    }

    @Test
    void allCompaniesWithoutClient_ReturnListOfOne(){
        when(companyRepository.findByClientIsNull())
                .thenReturn(List.of(company));

        List<Company> result = service.allCompaniesWithoutClient();

        verify(companyRepository).findByClientIsNull();
        assertEquals(1, result.size());
    }
    @Test
    @WithAnonymousUser
    void updateCompany_Error() throws AccessDeniedException {

        EditCompanyDto dto = new EditCompanyDto();
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("test@za.za");
        clientDto.setPhone("0896619422");
        clientDto.setFirstName("test");
        clientDto.setLastName("test");
        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!"))
                .when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> service.updateCompany(dto, false, dto.getClientId()));

        verify(companyRepository, never()).findByName(anyString());
        verify(modelMapper, never()).map(ArgumentMatchers.any(AddCompanyDto.class), eq(Company.class));


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCompany_ErrorMessage() throws AccessDeniedException {

        EditCompanyDto dto = new EditCompanyDto();
        dto.setUic(company.getUic());
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber("202212212");
        dto.setAccountablePerson(company.getAccountablePerson());

        Client client = new Client();
        client.setEmail("test@za.za");
        client.setPhone("0896619422");
        client.setFirstName("test");
        client.setLastName("test");
        client.setId(UUID.randomUUID());
        dto.setClientId(UUID.randomUUID());
        company.setClient(new Client());
        doNothing().when(userService).isUserLogIn();
        when(companyRepository
                .findById(dto.getId())).thenReturn(Optional.of(company));

        when(clientRepository.findById(dto.getClientId())).thenReturn(Optional.of(client));
        HashMap<String,String> result = service.updateCompany(dto,true, dto.getClientId());

        verify(companyRepository, times(1)).findById(dto.getId());
        verify(clientRepository, times(1)).findById(dto.getClientId());

        assertNotEquals(client.getId(), companyRepository.findById(company.getId()).get().getClient().getId());
        assertEquals("error", result.get("status"));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCompany_SuccessMessage() throws AccessDeniedException {

        EditCompanyDto dto = new EditCompanyDto();
        dto.setUic(company.getUic());
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber("202212212");
        dto.setAccountablePerson(company.getAccountablePerson());

        Client client = new Client();
        client.setEmail("test@za.za");
        client.setPhone("0896619422");
        client.setFirstName("test");
        client.setLastName("test");
        client.setId(UUID.randomUUID());
        dto.setClientId(UUID.randomUUID());

        doNothing().when(userService).isUserLogIn();
        when(companyRepository
                .findById(dto.getId())).thenReturn(Optional.of(company));

        when(clientRepository.findById(dto.getClientId())).thenReturn(Optional.of(client));
        HashMap<String,String> result = service.updateCompany(dto,true, dto.getClientId());

        verify(companyRepository, times(1)).findById(dto.getId());
        verify(clientRepository, times(1)).findById(dto.getClientId());

        assertEquals(client.getId(), companyRepository.findById(company.getId()).get().getClient().getId());
        assertEquals("success", result.get("status"));

    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCompanyWithClient_SuccessMessage() throws AccessDeniedException {

        EditCompanyDto dto = new EditCompanyDto();
        dto.setUic(company.getUic());
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber("202212212");
        dto.setAccountablePerson(company.getAccountablePerson());
        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("test@za.za");
        clientDto.setPhone("0896619422");
        clientDto.setFirstName("test");
        clientDto.setLastName("test");
        clientDto.setId(UUID.randomUUID());
        doNothing().when(userService).isUserLogIn();
        when(companyRepository
                .findById(dto.getId())).thenReturn(Optional.of(company));

        HashMap<String,String> result = service.updateCompany(dto,false, dto.getClientId());

        verify(companyRepository, times(1)).findById(dto.getId());
        assertEquals(dto.getVatNumber(), companyRepository.findById(company.getId()).get().getVatNumber());
        assertEquals("success", result.get("status"));

    }

    @Test
    @WithAnonymousUser
    void saveCompany_Error() throws AccessDeniedException {

        AddCompanyDto dto = new AddCompanyDto();
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!"))
                .when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> service.saveCompany(dto));

        verify(companyRepository, never()).findByName(anyString());
        verify(modelMapper, never()).map(ArgumentMatchers.any(AddCompanyDto.class), eq(Company.class));


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCompany_ErrorMessage() throws AccessDeniedException {

        AddCompanyDto dto = new AddCompanyDto();
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        when(companyRepository.findByName(dto.getName())).thenReturn(Optional.of(company));
        HashMap<String,String> result = service.saveCompany(dto);
        verify(companyRepository, times(1)).findByName(anyString());
        verify(modelMapper, times(0)).map(ArgumentMatchers.any(AddCompanyDto.class), eq(Company.class));
        assertEquals("error", result.get("status"));

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCompany_SuccessMessage() throws AccessDeniedException {

        AddCompanyDto dto = new AddCompanyDto();
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        when(companyRepository.findByName(dto.getName()))
                .thenReturn(Optional.empty());
        HashMap<String,String> result = service.saveCompany(dto);
        verify(companyRepository, times(1)).findByName(anyString());
        verify(modelMapper, times(1)).map(ArgumentMatchers.any(AddCompanyDto.class), eq(Company.class));
        assertEquals("success", result.get("status"));

    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCompany_SuccessMessage() throws AccessDeniedException {

        CompanyListDto dto = new CompanyListDto();
        dto.setId(company.getId());
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(userService.isAdmin(admin)).thenReturn(true);

        when(companyRepository.findById(dto.getId()))
                .thenReturn(Optional.of(company));

        HashMap<String,String> result = service.deleteCompany(String.valueOf(company.getId()));
        verify(companyRepository, times(1)).findById(dto.getId());
        verify(companyRepository, times(1)).saveAndFlush(company);
        assertNotEquals(company.getDeletedAt(), null);
        assertEquals("success", result.get("status"));

    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    void deleteCompanyByEditor_AccessDenied() throws AccessDeniedException {

        CompanyListDto dto = new CompanyListDto();
        dto.setId(company.getId());
        dto.setUic(company.getUic());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setVatNumber(company.getVatNumber());
        dto.setAccountablePerson(company.getAccountablePerson());
        when(userService.getLoggedInUser()).thenReturn(Optional.of(editor));
        when(userService.isAdmin(editor)).thenReturn(false);


        assertThrows(AccessDeniedException.class, () -> service.deleteCompany(String.valueOf(company.getId())));

        verify(companyRepository, never()).findById(dto.getId());
        verify(companyRepository, never()).saveAndFlush(company);
        assertNull(company.getDeletedAt());



    }

}
