package com.example.mkalinova.user;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.access.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
	
	@Mock
	private ModelMapper modelMapper;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passEn;
	private User administrator;
	private User editor;
	private AddUserDto newUser;
	private User newUserRepo;
	@InjectMocks
	private UserServiceImpl userService;
	
	@BeforeEach
	void setUp() {
		administrator = new User();
		administrator.setId(UUID.fromString("e1b27c6e-1b7a-4f69-a9e0-5f38e2c3f3a4"));
		administrator.setFirstName("Mirela");
		administrator.setLastName("Kalinova");
		administrator.setUsername("admin");
		administrator.setEmail("kalinova@test.bg");
		administrator.setPassword("TestSame2314!");
		administrator.setRole(UsersRole.ADMIN);
		
		editor = new User();
		editor.setId(UUID.fromString("e1b27c6e-1b7a-5f69-a9e0-5f38e2c3f3a4"));
		editor.setFirstName("Mirela");
		editor.setUsername("editor");
		editor.setLastName("Kalinova");
		editor.setEmail("kalinova@test.bg");
		editor.setPassword("TestSame2314!");
		editor.setRole(UsersRole.EDITOR);
		
		newUser = new AddUserDto();
		newUser.setFirstName("Mirela");
		newUser.setLastName("Kalinova");
		newUser.setUsername("test");
		newUser.setEmail("kalinova@test.bg");
		newUser.setPassword("TestSame2314!");
		newUser.setRole("editor");
		newUserRepo = new User();
		newUserRepo.setFirstName("Mirela");
		newUserRepo.setLastName("Kalinova");
		newUserRepo.setUsername("test");
		newUserRepo.setEmail("kalinova@test.bg");
		newUserRepo.setPassword("TestSame2314!");
		newUserRepo.setRole(UsersRole.EDITOR);
		
	}
	
	void mockLoggedInUser(String username) {
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password", new ArrayList<>());
		
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		
		SecurityContextHolder.setContext(securityContext);
	}
	
	@Test
	void isAdminTrue() {
		when(userRepository.findByUsername(administrator.getUsername())).thenReturn(administrator);
		assertTrue(userService.isAdmin(administrator));
		verify(userRepository, times(1)).findByUsername(administrator.getUsername());
	}
	
	@Test
	void getLoggedInUserIdTestSuccess() throws AccessDeniedException {
		mockLoggedInUser("test");
		when(userRepository.findByUsername("test")).thenReturn(administrator);
		UUID id = userService.getLoggedInUserId();
		UUID existingId = UUID.fromString("e1b27c6e-1b7a-4f69-a9e0-5f38e2c3f3a4");
		assertEquals(existingId, id);
		verify(userRepository).findByUsername("test");
		
	}
	
	@Test
	void addNewUserByAdminAndSuccess() throws AccessDeniedException {
		mockLoggedInUser("admin");
		when(userRepository.findByUsername("admin")).thenReturn(administrator);
		UUID id = userService.getLoggedInUserId();
		UUID expectedId = UUID.fromString("e1b27c6e-1b7a-4f69-a9e0-5f38e2c3f3a4");
		assertEquals(expectedId, id);
		
		boolean result = userService.isAdmin(administrator);
		assertTrue(result);
		when(modelMapper.map(any(), eq(User.class))).thenReturn(administrator);
		verify(userRepository, times(2)).findByUsername(administrator.getUsername());
		userRepository.save(new User());
		ArrayList<String> success = userService.addNewUser(newUser);
		assertEquals("success", success.get(0));
		
	}
	
	@Test
	void addNewUserByAdminUserExist() throws AccessDeniedException {
		mockLoggedInUser("admin");
		when(userRepository.findByUsername("admin")).thenReturn(administrator);
		UUID id = userService.getLoggedInUserId();
		UUID existingId = UUID.fromString("e1b27c6e-1b7a-4f69-a9e0-5f38e2c3f3a4");
		assertEquals(existingId, id);
		boolean result = userService.isAdmin(administrator);
		assertTrue(result);
		when(userRepository.findByUsernameOrEmail(newUserRepo.getUsername(), newUser.getEmail())).thenReturn(Optional.of(newUserRepo));
		boolean isUserExist = userService.userByUsernameOrEmail(newUserRepo.getUsername(), newUser.getEmail());
		assertTrue(isUserExist);
		
		when(modelMapper.map(any(), eq(User.class))).thenReturn(administrator);
		System.out.println(newUser.getUsername());
		ArrayList<String> success = userService.addNewUser(newUser);
		assertEquals("error", success.get(0));
		verify(userRepository, atLeastOnce()).findByUsername("admin");
		verify(userRepository, never()).save(any());
	}
	
	@Test
	void addNewUserByLogoutUserAndThrow() throws AccessDeniedException {
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn("anonymous"); // not UserDetails
		
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		
		assertThrows(AccessDeniedException.class, () -> {
			userService.getLoggedInUserId();
		});
	}
	
	@Test
	void addNewUserByEditor() throws AccessDeniedException {
		mockLoggedInUser("editor");
		when(userRepository.findByUsername("editor")).thenReturn(editor);
		UUID id = userService.getLoggedInUserId();
		UUID expectedId = UUID.fromString("e1b27c6e-1b7a-5f69-a9e0-5f38e2c3f3a4");
		assertEquals(expectedId, id);
		when(modelMapper.map(any(), eq(User.class))).thenReturn(editor);
		boolean result = userService.isAdmin(editor);
		assertFalse(result);
		
		verify(userRepository, times(2)).findByUsername(editor.getUsername());
		
		assertThrows(AccessDeniedException.class, () -> {
			userService.addNewUser(newUser);
		});
	}
	
	@Test
	void getAllUsers() {
		// Подготовка на тестови данни
		User user1 = new User();
		user1.setRole(UsersRole.ADMIN);
		User user2 = new User();
		user2.setRole(UsersRole.EDITOR);
		
		List<User> users = Arrays.asList(user1, user2);
		when(userRepository.findAll()).thenReturn(users);
		
		when(modelMapper.map(user1, AddUserDto.class)).thenReturn(new AddUserDto());
		when(modelMapper.map(user2, AddUserDto.class)).thenReturn(new AddUserDto());
		
		List<AddUserDto> result = userService.getAll(AddUserDto.class);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(modelMapper, times(1)).map(user1, AddUserDto.class);
		verify(modelMapper, times(1)).map(user2, AddUserDto.class);
	}
	
	@Test
	void getByIdSuccess() {
		when(userRepository.findById(administrator.getId())).thenReturn(Optional.of(administrator));
		EditUserDto editUserDto = new EditUserDto();
		editUserDto.setId(UUID.randomUUID());
		when(modelMapper.map(administrator, EditUserDto.class)).thenReturn(editUserDto);
		EditUserDto dto = userService.getById(administrator.getId(), EditUserDto.class);
		assertNotNull(dto);
		assertEquals(editUserDto, dto);
		verify(userRepository, times(1)).findById(administrator.getId());
		verify(modelMapper, times(1)).map(administrator, EditUserDto.class);
	}
	
	
	@Test
	void getByIdNull() {
		when(userRepository.findById(administrator.getId())).thenReturn(Optional.empty());
		
		EditUserDto dto = userService.getById(administrator.getId(), EditUserDto.class);
		assertNull(dto);
		
		verify(userRepository, atLeastOnce()).findById(administrator.getId());
		verifyNoInteractions(modelMapper);
	}
	
	@Test
	void deleteUserByAdmin() throws AccessDeniedException {
		mockLoggedInUser("admin");
		when(userRepository.findByUsername("admin")).thenReturn(administrator);
		UUID id = userService.getLoggedInUserId();
		UUID existingId = UUID.fromString("e1b27c6e-1b7a-4f69-a9e0-5f38e2c3f3a4");
		assertEquals(existingId, id);
	}
	
}
