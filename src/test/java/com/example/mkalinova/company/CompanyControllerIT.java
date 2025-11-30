package com.example.mkalinova.company;


import com.example.mkalinova.app.client.data.dto.ClientListCarDto;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.client.repo.ClientRepository;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.company.repo.CompanyRepository;
import com.example.mkalinova.app.company.service.CompanyServiceImpl;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class CompanyControllerIT {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private UserRepository userRepository;
	
	@InjectMocks
	private CompanyServiceImpl service;
	
	private User admin;
	private User editor;
	private Company company;
	private Company companySecond;
	private Client client;
	
	@BeforeEach
	void setUp() {
		companyRepository.deleteAll();
		userRepository.deleteAll();
		clientRepository.deleteAll();
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
		
		client = new Client();
		client.setPhone("0896619422");
		client.setFirstName("Test");
		client.setLastName("Test");
		client.setEmail("Test@test.bg");
		clientRepository.saveAndFlush(client);
		userRepository.saveAndFlush(admin);
		userRepository.saveAndFlush(editor);
		company = new Company();
		company.setName("test");
		company.setUic("201478523");
		company.setVatNumber("BG201478523");
		company.setAddress("Test address");
		company.setAccountablePerson("Test Test");
		
		companySecond = new Company();
		companySecond.setName("test2");
		companySecond.setUic("201478524");
		companySecond.setVatNumber("BG201478524");
		companySecond.setAddress("Test address");
		companySecond.setAccountablePerson("Test Test");
		companySecond.setDeleteAd(LocalDateTime.now());
		companySecond.setClient(client);
		companyRepository.saveAndFlush(company);
		companyRepository.saveAndFlush(companySecond);
	}
	
	@Test
	public void getCompaniesList() throws Exception {
		ArrayList<CompanyListDto> list = new ArrayList<>();
		list.add(modelMapper.map(company, CompanyListDto.class));
		
		list.add(modelMapper.map(companySecond, CompanyListDto.class));
		mockMvc.perform(get("/company/list")
						.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(model().attribute("companies", hasSize(1)));
	}
	
	@Test
	public void addCompany() throws Exception {
		List<ClientListCarDto> list = new ArrayList<>();
		list.add(modelMapper.map(client, ClientListCarDto.class));
		
		mockMvc.perform(get("/company/add")
						.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(model().attribute("clients", hasSize(1)));
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createCompany_WithValidCompanyDtoWithoutClient_RedirectsToCompanyList() throws Exception {
		
		mockMvc.perform(post("/company/add")
						
						.param("name", "Test123")
						.param("uic", "205205205")
						.param("vatNumber", "bg205205205")
						.param("accountablePerson", "Test")
						.param("address", "Test")
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/list"));
		
		
		Optional<Company> newCompany = companyRepository.findByName("Test123");
		assertTrue(newCompany.isPresent());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createCompany_WithNonValidCompanyDtoWithoutClient_RedirectsToCompanyEdit() throws Exception {
		
		mockMvc.perform(post("/company/add")
						
						.param("name", "Test123")
						.param("uic", "2052052")
						.param("vatNumber", "bg205205205")
						.param("accountablePerson", "Test")
						.param("address", "Test")
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/add"));
		
		Optional<Company> newCompany = companyRepository.findByName("Test123");
		assertFalse(newCompany.isPresent());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void createCompany_WithValidCompanyDtoWithClient_RedirectsToCompanyList() throws Exception {
		
		mockMvc.perform(post("/company/add")
						
						.param("name", "Test133")
						.param("uic", "207207207")
						.param("vatNumber", "bg207207207")
						.param("accountablePerson", "Test")
						.param("address", "Test")
						.param("clientId", String.valueOf(client.getId()))
						.param("client.id", String.valueOf(client.getId()))
						.param("client.firstName", client.getFirstName())
						.param("client.lastName", client.getLastName())
						.param("client.phone", client.getPhone())
						.param("client.email", client.getEmail())
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/list"));
		
		Optional<Company> newCompany = companyRepository.findByName("Test133");
		assertTrue(newCompany.isPresent());
		assertEquals(client.getId(), newCompany.get().getClient().getId());
		
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editCompany_WithValidCompanyDtoWithClient_RedirectsToCompanyList() throws Exception {
		
		mockMvc.perform(post("/company/edit/{id}", company.getId())
						
						.param("name", "SomeNewName")
						.param("uic", company.getUic())
						.param("vatNumber", company.getVatNumber())
						.param("accountablePerson", company.getAccountablePerson())
						.param("address", company.getAddress())
						.param("clientId", String.valueOf(client.getId()))
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/list"));
		
		Optional<Company> updatedCompany = companyRepository.findByName("SomeNewName");
		assertTrue(updatedCompany.isPresent());
		
		assertEquals(client.getId(), updatedCompany.get().getClient().getId());
		assertEquals(updatedCompany.get().getId(), company.getId());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editCompany_WithValidCompanyDtoWithoutClient_RedirectsToCompanyList() throws Exception {
		
		mockMvc.perform(post("/company/edit/{id}", company.getId())
						
						.param("name", "SomeNewName")
						.param("uic", company.getUic())
						.param("vatNumber", company.getVatNumber())
						.param("accountablePerson", company.getAccountablePerson())
						.param("address", company.getAddress())
						.param("clientId", "")
						
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/list"));
		
		Optional<Company> updatedCompany = companyRepository.findByName("SomeNewName");
		assertTrue(updatedCompany.isPresent());
		
		assertNull(updatedCompany.get().getClient());
		assertEquals(updatedCompany.get().getId(), company.getId());
		
		
	}
	
	@Test
	@WithMockUser(username = "editor", roles = "EDITOR")
	public void deleteCompanyByEditor_AccessDenied() throws Exception {
		
		UUID id = company.getId();
		mockMvc.perform(post("/company/delete/{id}", id)
						.param("id", String.valueOf(id))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		Optional<Company> optCompany = companyRepository.findById(id);
		assertNull(optCompany.get().getDeletedAt());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void deleteCompanyByAdmin_Success() throws Exception {
		
		
		mockMvc.perform(post("/company/delete/{id}", company.getId())
						
						
						.param("id", String.valueOf(company.getId()))
						.with(csrf()))
				
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/company/list"));
		
		
		Optional<Company> optCompany = companyRepository.findById(company.getId());
		assertNotNull(optCompany.get().getDeletedAt());
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void removeClientFromCompanyByAdmin_Error_NonValidCompanyId() throws Exception {
		
		mockMvc.perform(post("/company/remove-client/{id}", client.getId())
						
						
						.param("companyId", String.valueOf(UUID.randomUUID()))
						.param("id", String.valueOf(client.getId()))
						.with(csrf()))
				
				.andExpect(status().is4xxClientError());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void removeClientFromCompanyByAdmin_Error_NonValidClientId() throws Exception {
		mockMvc.perform(post("/company/remove-client/{id}", client.getId())
						
						
						.param("companyId", String.valueOf(company.getId()))
						.param("id", String.valueOf(UUID.randomUUID()))
						.with(csrf()))
				
				.andExpect(status().is4xxClientError());
		
		
	}
	
}
