package com.example.mkalinova.app.land.Controller;

import com.example.mkalinova.app.order.service.OrderService;
import com.example.mkalinova.app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class HomeController extends BaseController {
	private final OrderService orderService;
	private final UserService userService;
	
	public HomeController(OrderService orderService, UserService userService) {
		this.orderService = orderService;
		this.userService = userService;
	}
	
	@GetMapping("/")
	public ModelAndView index() {
		log.debug("Attempt to get index page..");
		return super.view("fragments/index");
		
	}
	@GetMapping("/dashboard")
	public ModelAndView dashboard() {
		log.debug("Attempt to get index page..");
		ModelAndView modelAndView =  super.view("order/list");
		modelAndView.addObject("orderList", orderService.getOrders(20));
		modelAndView.addObject("heading","Последни поръчки");
		
		return modelAndView;
		
	}
}
