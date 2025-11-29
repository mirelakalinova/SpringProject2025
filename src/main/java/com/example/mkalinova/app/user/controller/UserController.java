package com.example.mkalinova.app.user.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.user.data.dto.AddUserDto;
import com.example.mkalinova.app.user.data.dto.EditUserDto;
import com.example.mkalinova.app.user.data.dto.UserListDto;
import com.example.mkalinova.app.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController extends BaseController {
	private final UserServiceImpl userService;
	
	public UserController(UserServiceImpl userService) {
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
	
	@PostMapping("/logout")
	public ModelAndView logout() {
		return super.view("/");
	}
	
	@GetMapping("/users")
	public ModelAndView userList() {
		ModelAndView modelAndView = super.view("user/users");
		modelAndView.addObject("users", userService.getAll(UserListDto.class));
		return modelAndView;
	}
	
	@GetMapping("/add-user")
	public ModelAndView addUser() {
		return super.view("user/add-user");
	}
	
	@PostMapping("/add-user")
	public String createUser(@Valid AddUserDto addUserDto, BindingResult bindingResult, RedirectAttributes attributes) throws AccessDeniedException {
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
	public ModelAndView editUser(@PathVariable UUID id, Model model) {
		ModelAndView modelAndView = super.view("user/edit-user");
		if (!model.containsAttribute("editUserDto")) {
			modelAndView.addObject("editUserDto", userService.getById(id, EditUserDto.class));
		}
		
		return modelAndView;
	}
	
	@PostMapping("edit-user/{id}")
	public String editUser(@Valid
	                       EditUserDto editUserDto,
	                       BindingResult bindingResult,
	                       RedirectAttributes attributes) throws AccessDeniedException {
		
		if (bindingResult.hasErrors()) {
			attributes.addFlashAttribute("editUserDto", editUserDto);
			attributes.addFlashAttribute("org.springframework.validation.BindingResult.editUserDto", bindingResult);
			return "redirect:/edit-user/" + editUserDto.getId();
		}
		HashMap<String, String> result = userService.editUser(editUserDto.getId(), editUserDto);
		
		
		attributes.addFlashAttribute("message", result.get("message"));
		attributes.addFlashAttribute("status", result.get("status"));
		return "redirect:/users";
	}
	
	
	@PostMapping("/delete/{id}")
	public String deleteUser(@PathVariable String id,
	                         RedirectAttributes attributes) throws AccessDeniedException {
		UUID uuid = UUID.fromString(id);
		HashMap<String, String> result = userService.deleteUser(uuid);
		attributes.addFlashAttribute("message", result.get("message"));
		attributes.addFlashAttribute("status", result.get("status"));
		
		
		return "redirect:/users";
	}
	
	
}
