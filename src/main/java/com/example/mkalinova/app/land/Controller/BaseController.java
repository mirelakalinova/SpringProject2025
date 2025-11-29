package com.example.mkalinova.app.land.Controller;

import org.springframework.web.servlet.ModelAndView;

public class BaseController {
	protected ModelAndView view(String view) {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("fragments/base-layout.html");
		modelAndView.addObject("view", view);
		return modelAndView;
		
	}
}
