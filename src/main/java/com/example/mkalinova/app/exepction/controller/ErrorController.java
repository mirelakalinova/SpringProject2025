package com.example.mkalinova.app.exepction.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController extends BaseController {
	
	@GetMapping("/access-denied")
	public ModelAndView genericError() {
		return super.view("errors/access-denied");
	}
}
