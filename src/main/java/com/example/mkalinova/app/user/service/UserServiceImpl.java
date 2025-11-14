package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    public final ModelMapper modelMapper;
    public final UserRepository userRepository;
    public final PasswordEncoder passEn;



    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passEn) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passEn = passEn;
    }

    public Optional<User> getLoggedInUser() throws AccessDeniedException {
        return this.userRepository.findById(this.getLoggedInUserId());
    }

    @Override
    public void isUserLogIn() throws AccessDeniedException {
        Long id = getLoggedInUserId();
    }

    public Long getLoggedInUserId() throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = this.userRepository.findByUsername(username);
            return user.getId();
        } else {
            throw new AccessDeniedException("Нямате права да извършите тази операция!");
        }
    }

    public ArrayList<String> addNewUser(AddUserDto addUserDto) throws AccessDeniedException {
        ArrayList<String> result = new ArrayList<>();

        Optional<User> loggedUser =
                this.userRepository.findById(getLoggedInUserId());
        if(!isAdmin(modelMapper.map(loggedUser, User.class))){
          throw new AccessDeniedException("Нямате права да извършите тази операция!");

        }
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


    public boolean userByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }

    public <T> List<T> getAll(Class<T> clazz) {
        List<User> users = this.userRepository.findAll();
        List<T> dtoList = new ArrayList<>();
        for (User user : users) {
            T dtoUser = modelMapper.map(user, clazz);
            String role = user.getRole().label;

            dtoList.add(dtoUser);
        }
        return dtoList;
    }


    public <T> T getById(Long id, Class<T> clazz) {

        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, clazz))
                .orElse(null);
    }

    public HashMap<String, String> editUser(Long id, EditUserDto editUserDto) throws AccessDeniedException {


            HashMap<String, String> result = new HashMap<>();
            User userToEdit = this.getById(id, User.class);

            if (userToEdit == null) {
                result.put("message", "Няма такъв потребител!");
                result.put("status", "error");
                return result;
            }

            String firstName = editUserDto.getFirstName();
            String lastName = editUserDto.getLastName();
            String password = editUserDto.getPassword();
            Optional<User> loggedInUser = this.userRepository.findById(this.getLoggedInUserId());

               boolean isRoleChanged = editUserDto.getRole().equals(userToEdit.getRole().label);

               //Проверяваме дали потребителят е оторизиран да променя роли
               if (isAdmin(modelMapper.map(loggedInUser, User.class)) && !isRoleChanged) {
                   String role = editUserDto.getRole();
                   UsersRole userRole = UsersRole.findRole(role);

                   userToEdit.setRole(userRole);
               }
               if(!isAdmin(modelMapper.map(loggedInUser, User.class)) && !isRoleChanged){
                   throw new  AccessDeniedException("Нямате права да променяте роли на потребители!");
               }

            //Проверяваме дали е въведена парола
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

            return result;


    }

    public boolean isAdmin(User user) {

        return this.userRepository.findByUsername(user.getUsername()).getRole().name().equals("ADMIN");
    }



    public HashMap<String, String> deleteUser(Long id) throws AccessDeniedException {
        Optional<User> loggedInUser = this.userRepository.findById(this.getLoggedInUserId());
        HashMap<String,String > result = new HashMap<>();
        if(!isAdmin(modelMapper.map(loggedInUser, User.class))){
          throw new AccessDeniedException("Нямате права да изтриете този потребител!");

        }

        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()){
            this.userRepository.delete(modelMapper.map(user, User.class));
            result.put("message", "Успешно изтрит потребител: " + user.get().getUsername());
            result.put("status", "success");
            return result;
        } else {
            result.put("message", "Няма потребител с id: " + id);
            result.put("status", "error");
            return result;
        }
    }
}
