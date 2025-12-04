package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.entity.User;

import org.springframework.security.access.AccessDeniedException;
import java.util.*;

public interface UserService {
	UUID getLoggedInUserId() throws AccessDeniedException;
	
	ArrayList<String> addNewUser(AddUserDto addUserDto) throws AccessDeniedException;
	
	boolean userByUsernameOrEmail(String username, String email);
	
	<T> List<T> getAll(Class<T> clazz);
	
	<T> T getById(UUID id, Class<T> clazz);
	
	HashMap<String, String> editUser(UUID id, EditUserDto editUserDto) throws AccessDeniedException;
	
	boolean isAdmin(User user) throws AccessDeniedException;
	
	Optional<User> getLoggedInUser() throws AccessDeniedException;
	
	void isUserLogIn() throws AccessDeniedException;
	
	HashMap<String, String> blockUser(UUID uuid) throws AccessDeniedException;
	HashMap<String, String> unblockUser(UUID uuid) throws AccessDeniedException;
}
