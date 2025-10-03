package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
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
import java.util.Objects;

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
        result.add("Успешно добавен потребител!\n" +
                "Username: \n" + addUserDto.getUsername() + "Email: " + addUserDto.getEmail());


        return result;
    }

    public void editUser(){

    }

    public boolean userByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }

    public <T> List<T> getAll(Class<T> clazz) {
        List<User> users = this.userRepository.findAll();
        List<T> dtoList = new ArrayList<>();
        for (User user: users){
            T dtoUser = modelMapper.map(user, clazz);
            String role = user.getRole().label;

            dtoList.add(dtoUser);
        }
       return dtoList;
    }


}
