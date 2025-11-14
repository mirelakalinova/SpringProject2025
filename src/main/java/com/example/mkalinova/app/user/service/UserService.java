package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.entity.User;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Long getLoggedInUserId() throws AccessDeniedException;

    ArrayList<String> addNewUser(AddUserDto addUserDto) throws AccessDeniedException;

    boolean userByUsernameOrEmail(String username, String email);

    <T> List<T> getAll(Class<T> clazz);

    <T> T getById(Long id, Class<T> clazz);

    HashMap<String, String> editUser(Long id, EditUserDto editUserDto) throws AccessDeniedException;

    boolean isAdmin(User user) throws AccessDeniedException;

    Optional<User> getLoggedInUser() throws AccessDeniedException;

    void isUserLogIn() throws AccessDeniedException;
}
