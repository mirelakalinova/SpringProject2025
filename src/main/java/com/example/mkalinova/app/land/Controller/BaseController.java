package com.example.mkalinova.app.land.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

public class BaseController {
	@ModelAttribute("currentPath")
	public String currentPath(HttpServletRequest request) {
		if (request == null) return "";
		String uri = request.getRequestURI();
		
		return uri == null ? "no-url" : uri;
	}
	
	protected ModelAndView view(String view) {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("fragments/base-layout.html");
		modelAndView.addObject("view", view);
		return modelAndView;
		
	}
}
