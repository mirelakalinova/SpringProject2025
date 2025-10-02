package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    public final ModelMapper modelMapper;
    public final UserRepository userRepository;
    public final PasswordEncoder passEn;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passEn) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passEn = passEn;
    }

    public ArrayList<String> addNewUser(AddUserDto addUserDto) {
        ArrayList<String> result = new ArrayList<>();

        if (userByUsernameOrEmail(addUserDto.getUsername(), addUserDto.getEmail())) {
            result.addFirst("error");
            result.add("Вече съществува потребител с този имейл или с потребителско име!");
            return result;
        }

        User user = new User();
        user = modelMapper.map(addUserDto, User.class);
        String pass = passEn.encode(user.getPassword());
        user.setPassword(pass);
        user.setRole(UsersRole.valueOf(addUserDto.getRole().toUpperCase()));
        userRepository.save(user);
        result.addFirst("success");
        result.add("Успешно добавен потребител с username: " + addUserDto.getUsername() + " и email: " + addUserDto.getEmail());


        return result;
    }

    public void editUser(){

    }

    public boolean userByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }

    public List<User> getAll() {
       return this.userRepository.findAll();
    }
}
