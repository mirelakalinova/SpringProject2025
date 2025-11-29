package com.example.mkalinova.part;

import com.example.mkalinova.app.parts.data.dto.PartListDto;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PartControllerIT {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PartRepository partRepository;
	@Autowired
	private ModelMapper modelMapper;
	private User admin;
	private User editor;
	private Part partFirst;
	private Part partSecond;
	
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		partRepository.deleteAll();
		admin = new User();
		admin.setFirstName("Mirela");
		admin.setLastName("Kalinova");
		admin.setUsername("admin");
		admin.setEmail("admin@test.bg");
		admin.setPassword("Password1234!");
		admin.setRole(UsersRole.ADMIN);
		
		userRepository.save(admin);
		
		
		editor = new User();
		editor.setFirstName("editor");
		editor.setLastName("editor");
		editor.setUsername("editor");
		editor.setEmail("editor@test.bg");
		editor.setPassword("Password1234!");
		editor.setRole(UsersRole.EDITOR);
		userRepository.save(editor);
		
		partFirst = new Part();
		partFirst.setName("Test first part");
		partFirst.setPrice(120D);
		partSecond = new Part();
		partSecond.setPrice(140D);
		partFirst.setName("Test second part");
		partSecond.setDeletedAt(LocalDateTime.now());
		partRepository.save(partFirst);
		partRepository.save(partSecond);
	}
	
	@Test
	public void getPartList() throws Exception {
		ArrayList<PartListDto> list = new ArrayList<>();
		list.add(modelMapper.map(partFirst, PartListDto.class));
		list.add(modelMapper.map(partSecond, PartListDto.class));
		mockMvc.perform(get("/part/list").contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(model().attribute("parts", hasSize(1)));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addPart_Success() throws Exception {
		mockMvc.perform(post("/part/add")
						.param("name", "Test123")
						.param("price", String.valueOf(120d))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/part/list"));
		
		Optional<Part> part = partRepository.findByName("Test123");
		assertTrue(part.isPresent());
		assertEquals(120D, part.get().getPrice());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addPart_Error() throws Exception {
		mockMvc.perform(post("/part/add")
						.param("name", "Te")
						.param("price", String.valueOf(120d))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/part/add"));
		
		Optional<Part> part = partRepository.findByName("Test");
		assertFalse(part.isPresent());
		
	}
	
	@Test
	@WithAnonymousUser
	public void addPart_AccessDenied() throws Exception {
		mockMvc.perform(post("/part/add")
						.param("name", "Test")
						.param("price", String.valueOf(120d))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		Optional<Part> part = partRepository.findByName("Test");
		assertFalse(part.isPresent());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editPartByAdmin_Success() throws Exception {
		mockMvc.perform(post("/part/edit/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.param("name", "Test")
						.param("price", String.valueOf(110D))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/part/list"));
		
		Optional<Part> part = partRepository.findByName("Test");
		assertNotNull(part);
		assertEquals("Test", part.get().getName());
		assertEquals(110D, part.get().getPrice());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editPartByAdmin_Error() throws Exception {
		mockMvc.perform(post("/part/edit/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.param("name", "Te")
						.param("price", String.valueOf(110D))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/part/edit/" + partFirst.getId()));
		
		Optional<Part> part = partRepository.findByName("Test");
		assertTrue(part.isEmpty());
		
	}
	
	@Test
	@WithAnonymousUser
	public void editPartByAnonymous_AccessDenied() throws Exception {
		mockMvc.perform(post("/part/edit/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.param("name", "Test")
						.param("price", String.valueOf(110D))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		Optional<Part> part = partRepository.findByName("Test");
		assertTrue(part.isEmpty());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void DeletePartByAdmin_Success() throws Exception {
		mockMvc.perform(post("/part/delete/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/part/list"));
		
		Optional<Part> part = partRepository.findById(partFirst.getId());
		assertNotNull(part.get().getDeletedAt());
		
		
	}
	
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void DeletePartByEditor_AccessDenied() throws Exception {
		mockMvc.perform(post("/part/delete/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		Optional<Part> part = partRepository.findById(partFirst.getId());
		assertNull(part.get().getDeletedAt());
		
		
	}
	
	
	@Test
	@WithAnonymousUser
	public void DeletePartByAnonymous_AccessDenied() throws Exception {
		mockMvc.perform(post("/part/delete/{id}", partFirst.getId())
						.param("id", String.valueOf(partFirst.getId()))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		Optional<Part> part = partRepository.findById(partFirst.getId());
		assertNull(part.get().getDeletedAt());
		
		
	}
}