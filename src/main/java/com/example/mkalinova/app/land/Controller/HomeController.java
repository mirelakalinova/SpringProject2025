package com.example.mkalinova.app.land.Controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class HomeController extends BaseController {
	
	
	@GetMapping("/")
	public ModelAndView index() {
		log.debug("Attempt to get index page..");
		return super.view("fragments/index");
		
	}
}
