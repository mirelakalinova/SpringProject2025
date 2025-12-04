package com.example.mkalinova.user;


import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.dto.UserListDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;
	private User admin;
	private User editor;
	
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		admin = new User();
		admin.setFirstName("Mirela");
		admin.setLastName("Kalinova");
		admin.setUsername("admin");
		admin.setEmail("admin@test.bg");
		admin.setPassword("Password1234!");
		admin.setRole(UsersRole.ADMIN);
		admin.setEnabled(true);
		
		userRepository.save(admin);
		
		
		editor = new User();
		editor.setFirstName("editor");
		editor.setLastName("editor");
		editor.setUsername("editor");
		editor.setEmail("editor@test.bg");
		editor.setPassword("Password1234!");
		editor.setRole(UsersRole.EDITOR);
		editor.setEnabled(true);
		
		userRepository.save(editor);
		
		
	}
	
	@Test
	public void editUser_ReturnsModelAndViewWithUser() throws Exception {
		
		mockMvc.perform(
						get("/edit-user/{id}", admin.getId())
								.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("editUserDto"))
				.andExpect(model().attribute("editUserDto", instanceOf(EditUserDto.class)
				
				));
		
		
	}
	
	@Test
	public void getUserList() throws Exception {
		ArrayList<UserListDto> list = new ArrayList<>();
		list.add(modelMapper.map(admin, UserListDto.class));
		list.add(modelMapper.map(editor, UserListDto.class));
		mockMvc.perform(get("/users").contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(model().attribute("users", hasSize(2)));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addUserSucces() throws Exception {
		mockMvc.perform(post("/add-user")
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "test_editor")
						.param("email", "test@test.bg")
						.param("password", "Password1234!")
						.param("role", "EDITOR")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/add-user"));
		
		User user = userRepository.findByUsername("test_editor");
		assertNotNull(user);
		assertEquals("test@test.bg", user.getEmail());
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addUserWithErrors() throws Exception {
		mockMvc.perform(post("/add-user")
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "test_editor")
						.param("email", "test@test.bg")
						.param("password", "Password1234")
						.param("role", "EDITOR")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("addUserDto"))
				.andExpect(redirectedUrl("/add-user"));
		
		User user = userRepository.findByUsername("test_editor");
		assertNull(user);
		
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addUserWithExistingEmail() throws Exception {
		mockMvc.perform(post("/add-user")
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "test_editor")
						.param("email", "admin@test.bg")
						.param("password", "Password1234!")
						.param("role", "EDITOR")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("error"))
				.andExpect(redirectedUrl("/add-user"));
		
		Optional<User> user = userRepository.findByUsernameOrEmail("test_editor", "admin@test.bg");
		assertNotNull(user);
		
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addUserWithExistingUsername() throws Exception {
		mockMvc.perform(post("/add-user")
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "editor")
						.param("email", "test@test.bg")
						.param("password", "Password1234!")
						.param("role", "EDITOR")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("error"))
				.andExpect(redirectedUrl("/add-user"));
		
		Optional<User> user = userRepository.findByUsernameOrEmail("editor", "test@test.bg");
		assertNotNull(user);
		
		
	}
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void addUserByEditor_AccessDenied() throws Exception {
		mockMvc.perform(post("/add-user")
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "test_editor")
						.param("email", "test@test.bg")
						.param("password", "Password1234!")
						.param("role", "EDITOR")
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		
		User user = userRepository.findByUsername("test_editor");
		assertNull(user);
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editUserByAdmin_Success() throws Exception {
		mockMvc.perform(post("/edit-user/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "editor")
						.param("email", "editor@test.bg")
						.param("password", "Password1234!")
						.param("role", "Редактор")
						.with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/users"));
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		assertEquals("editor", user.getUsername());
		assertEquals("Test", user.getFirstName());
		
	}
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void editUserByEditor_Success() throws Exception {
		mockMvc.perform(post("/edit-user/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "editor")
						.param("email", "editor@test.bg")
						.param("password", "Password1234!")
						.param("role", "Редактор")
						.with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/users"));
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		assertEquals("editor", user.getUsername());
		assertEquals("Test", user.getFirstName());
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editUserByAdminChangeRole_Success() throws Exception {
		mockMvc.perform(post("/edit-user/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "editor")
						.param("email", "editor@test.bg")
						.param("password", "Password1234!")
						.param("role", "Администратор")
						.with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/users"));
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		assertEquals("editor", user.getUsername());
		assertEquals("Test", user.getFirstName());
		assertEquals("ADMIN", user.getRole().name());
		
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void editUserByAdminChangeRole_Return() throws Exception {
		mockMvc.perform(post("/edit-user/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.param("firstName", "Te")
						.param("lastName", "Te")
						.param("username", "editor")
						.param("email", "editor@test.bg")
						.param("password", "Password1234!")
						.param("role", "Администратор")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/edit-user/" + editor.getId()));
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		assertEquals("editor", user.getUsername());
		assertEquals("editor", user.getFirstName());
		assertEquals("EDITOR", user.getRole().name());
		
	}
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void editUserByEditorChangeRole_AccessDenied() throws Exception {
		mockMvc.perform(post("/edit-user/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.param("firstName", "Test")
						.param("lastName", "Test")
						.param("username", "editor")
						.param("email", "editor@test.bg")
						.param("password", "Password1234!")
						.param("role", "Администратор")
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		assertEquals("editor", user.getUsername());
		assertEquals("editor", user.getFirstName());
		assertEquals("EDITOR", user.getRole().name());
		
	}
	
	
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void DeleteUserByAdmin_Success() throws Exception {
		mockMvc.perform(post("/delete/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"));
		
		User user = userRepository.findByUsername("editor");
		assertNull(user);
		
		
	}
	
	
	@Test
	@WithMockUser(username = "editor", roles = {"EDITOR"})
	public void DeleteUserByEditor_AccessDenied() throws Exception {
		mockMvc.perform(post("/delete/{id}", editor.getId())
						.param("id", String.valueOf(editor.getId()))
						.with(csrf()))
				.andExpect(status().isForbidden());
		
		User user = userRepository.findByUsername("editor");
		assertNotNull(user);
		
		
	}
	
	@Test
	public void addUserViewAdmin() throws Exception {
		mockMvc.perform(get("/add-user").contentType(MediaType.TEXT_HTML)
				)
				.andExpect(status().isOk());
		
	}
	
	
	@Test
	
	public void loginView() throws Exception {
		mockMvc.perform(get("/login").contentType(MediaType.TEXT_HTML)
				)
				.andExpect(status().isOk());
		
	}
	
	
}
