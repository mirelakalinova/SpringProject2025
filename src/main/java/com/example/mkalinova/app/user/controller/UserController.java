package com.example.mkalinova.app.user.controller;

import com.example.mkalinova.app.Land.Controller.BaseController;
import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("addUserDto")
    public AddUserDto addUserDto() {
        return new AddUserDto();
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return super.view("user/login");
    }


    @GetMapping("/add-user")
    public ModelAndView addUser() {
        return super.view("user/add-user");
    }

    @PostMapping("/add-user")
    public String createUser(@Valid AddUserDto addUserDto, BindingResult bindingResult, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("addUserDto", addUserDto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.addUserDto", bindingResult);

            return "redirect:/add-user";
        }


        List<String> result = userService.addNewUser(addUserDto);
        String message = result.get(1);
        if ("error".equals(result.get(0))) {

            attributes.addFlashAttribute("error", message);
        } else if ("success".equals(result.get(0))) {
            attributes.addFlashAttribute("success", message);

        }
        return "redirect:/add-user";
    }


    @GetMapping("/edit-user/{id}")
    public ModelAndView editUser(@PathVariable Long id,
                           EditUserDto editUserDto,
                           RedirectAttributes attributes) {


        return super.view("user/edit-user/{id}");
    }

    @GetMapping("/users")
    public ModelAndView userList(){
        ModelAndView modelAndView = super.view("user/users");
        modelAndView.addObject("users", userService.getAll());
        return modelAndView;
    }
}
