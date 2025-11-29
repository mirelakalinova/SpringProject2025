package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	public final ModelMapper modelMapper;
	public final UserRepository userRepository;
	public final PasswordEncoder passEn;
	
	
	public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passEn) {
		this.modelMapper = modelMapper;
		this.userRepository = userRepository;
		this.passEn = passEn;
	}
	
	public Optional<User> getLoggedInUser() throws AccessDeniedException {
		log.debug("Attempt to get logged in user..");
		return this.userRepository.findById(this.getLoggedInUserId());
	}
	
	@Override
	public void isUserLogIn() throws AccessDeniedException {
		UUID id = getLoggedInUserId();
	}
	
	public UUID getLoggedInUserId() throws AccessDeniedException {
		log.debug("Attempt to get logged in user id..");
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails userDetails) {
			String username = userDetails.getUsername();
			User user = this.userRepository.findByUsername(username);
			log.info("Successfully return logged in user with id {}", user.getId());
			return user.getId();
		} else {
			
			throw new AccessDeniedException("Нямате права да извършите тази операция!");
		}
	}
	
	public ArrayList<String> addNewUser(AddUserDto addUserDto) throws AccessDeniedException {
		log.debug("Attempt to add new user with username {}", addUserDto.getUsername());
		ArrayList<String> result = new ArrayList<>();
		
		Optional<User> loggedUser =
				this.userRepository.findById(getLoggedInUserId());
		if (!isAdmin(modelMapper.map(loggedUser, User.class))) {
			
			
			throw new AccessDeniedException("Нямате права да извършите тази операция!");
			
		}
		if (userByUsernameOrEmail(addUserDto.getUsername(), addUserDto.getEmail())) {
			result.add("error");
			result.add("Вече съществува потребител с този имейл или с потребителско име!");
			log.warn("Return error message: user with username {} already exists", addUserDto.getUsername());
			
			return result;
		}
		
		User user = new User();
		user = modelMapper.map(addUserDto, User.class);
		String pass = passEn.encode(user.getPassword());
		user.setPassword(pass);
		user.setRole(UsersRole.valueOf(addUserDto.getRole().toUpperCase()));
		userRepository.save(user);
		result.add("success");
		result.add("Успешно добавен потребител!\n" +
				"Username: \n" + addUserDto.getUsername() + "Email: " + addUserDto.getEmail());
		
		log.info("Successfully added new user with username {}", addUserDto.getUsername());
		return result;
	}
	
	
	public boolean userByUsernameOrEmail(String username, String email) {
		log.debug("Attempt to find a user with username {} or email {}", username, email);
		
		return userRepository.findByUsernameOrEmail(username, email).isPresent();
	}
	
	public <T> List<T> getAll(Class<T> clazz) {
		log.debug("Attempt to get all users");
		List<User> users = this.userRepository.findAll();
		List<T> dtoList = new ArrayList<>();
		for (User user : users) {
			T dtoUser = modelMapper.map(user, clazz);
			String role = user.getRole().label;
			
			dtoList.add(dtoUser);
		}
		log.info("Successfully get all users");
		return dtoList;
	}
	
	
	public <T> T getById(UUID id, Class<T> clazz) {
		log.debug("Attempt to get user with id {}", id);
		return userRepository.findById(id)
				.map(user -> modelMapper.map(user, clazz))
				.orElse(null);
	}
	
	public HashMap<String, String> editUser(UUID id, EditUserDto editUserDto) throws AccessDeniedException {
		log.debug("Attempt to edit user with id {}", id);
		
		HashMap<String, String> result = new HashMap<>();
		User userToEdit = this.getById(id, User.class);
		
		if (userToEdit == null) {
			result.put("message", "Няма такъв потребител!");
			result.put("status", "error");
			log.warn("Return error message - user with id {} does not exist ", id);
			
			return result;
		}
		
		String firstName = editUserDto.getFirstName();
		String lastName = editUserDto.getLastName();
		String password = editUserDto.getPassword();
		Optional<User> loggedInUser = this.userRepository.findById(this.getLoggedInUserId());
		
		boolean isRoleChanged = editUserDto.getRole().equals(userToEdit.getRole().label);
		
		if (isAdmin(modelMapper.map(loggedInUser, User.class)) && !isRoleChanged) {
			String role = editUserDto.getRole();
			UsersRole userRole = UsersRole.findRole(role);
			
			userToEdit.setRole(userRole);
		}
		if (!isAdmin(modelMapper.map(loggedInUser, User.class)) && !isRoleChanged) {
			throw new AccessDeniedException("Нямате права да променяте роли на потребители!");
		}
		

		if (!password.isEmpty()) {
			String newPassword = editUserDto.getPassword();
			String encodedPassword = passEn.encode(newPassword);
			userToEdit.setPassword(encodedPassword);
		}
		
		userToEdit.setFirstName(firstName);
		userToEdit.setLastName(lastName);
		
		this.userRepository.save(userToEdit);
		
		result.put("message", "Успешно редактиран потребител: " + userToEdit.getUsername());
		result.put("status", "success");
		log.info("Successfully edited user with id {}", id);
		
		return result;
		
		
	}
	
	public boolean isAdmin(User user) {
		log.debug("Attempt check if user with id {} is admin..", user.getId());
		
		return this.userRepository.
				findByUsername(user.getUsername()).getRole().name().equals("ADMIN");
		
	}
	
	
	public HashMap<String, String> deleteUser(UUID id) throws AccessDeniedException {
		log.debug("Attempt delete if user with id {}", id);
		Optional<User> loggedInUser = this.userRepository.findById(this.getLoggedInUserId());
		HashMap<String, String> result = new HashMap<>();
		if (!isAdmin(modelMapper.map(loggedInUser, User.class))) {
			
			throw new AccessDeniedException("Нямате права да изтриете този потребител!");
			
		}
		
		Optional<User> user = this.userRepository.findById(id);
		
		if (user.isPresent()) {
			this.userRepository.delete(modelMapper.map(user, User.class));
			result.put("message", "Успешно изтрит потребител: " + user.get().getUsername());
			result.put("status", "success");
			log.info("Successfully deleted user with id {}", id);
			return result;
		} else {
			
			log.warn("Return error message: User with id {} does not exist", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Няма потребител с #: " + id);
			
		}
	}
}
