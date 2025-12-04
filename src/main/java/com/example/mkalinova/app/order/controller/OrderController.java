package com.example.mkalinova.app.order.controller;

import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.EditOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.service.OrderService;
import com.example.mkalinova.app.orderPart.service.OrderPartService;
import com.example.mkalinova.app.orderRepair.service.OrderRepairService;
import com.example.mkalinova.app.parts.service.PartService;
import com.example.mkalinova.app.repair.service.RepairService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {
	
	private final CarService carService;
	private final CompanyService companyService;
	private final ClientService clientService;
	private final PartService partService;
	private final RepairService repairService;
	private final OrderService orderService;
	private final OrderRepairService orderRepairService;
	private final OrderPartService orderPartService;
	
	
	public OrderController(CarService carService, CompanyService companyService, ClientService clientService, PartService partService, RepairService repairService, OrderService orderService, OrderRepairService orderRepairService, OrderPartService orderPartService) {
		this.carService = carService;
		this.companyService = companyService;
		this.clientService = clientService;
		this.partService = partService;
		this.repairService = repairService;
		this.orderService = orderService;
		this.orderRepairService = orderRepairService;
		this.orderPartService = orderPartService;
	}
	
	@GetMapping("/add")
	public ModelAndView addOrder(Model model) throws JsonProcessingException {
		
		ModelAndView modelAndView = super.view("order/add");
		modelAndView.addObject("parts", partService.getAllPartsByDeletedAtNull());
		modelAndView.addObject("repairs", repairService.getAllServicesByDeletedAtNull());
		if (!model.containsAttribute("orderDto")) {
			modelAndView.addObject("orderDto", new AddOrderDto());
		}
		AddOrderDto dto = (AddOrderDto) model.asMap().get("orderDto");
		if (dto != null) {
			modelAndView.addObject("dto", dto);
			modelAndView.addObject("dtoCar", dto.getCar());
			modelAndView.addObject("dtoClient", dto.getClient());
			modelAndView.addObject("dtoCompany", dto.getCompany());
			modelAndView.addObject("dtoParts", dto.getParts());
			modelAndView.addObject("dtoRepairs", dto.getRepairs());
			modelAndView.addObject("dtoSubtotal", dto.getSubtotal());
			modelAndView.addObject("dtoTax", dto.getTax());
			modelAndView.addObject("dtoDiscount", dto.getDiscount());
			modelAndView.addObject("discountAmount", dto.getDiscountAmount());
			modelAndView.addObject("discountPercent", dto.getDiscountPercent());
			modelAndView.addObject("dtoTotal", dto.getTotal());
			modelAndView.addObject("dtoNote", dto.getNote());
		}
		return modelAndView;
	}
	
	@PostMapping("/add")
	public String addNewOrder(@Valid AddOrderDto orderDto,
	                          BindingResult bindingResult,
	                          RedirectAttributes redirectAttributes) throws AccessDeniedException {
		
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("orderDto", orderDto);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderDto", bindingResult);
			return "redirect:/order/add";
		}
		
		HashMap<String, String> result = orderService.saveOrder(orderDto);
		if (result.get("status").equals("error")) {
			redirectAttributes.addFlashAttribute("orderDto", orderDto);
		}
		return "redirect:/order/add";
		
	}
	
	@GetMapping("/list")
	public ModelAndView orderList() throws AccessDeniedException {
		ModelAndView modelAndView = super.view("order/list");
		List<OrderListDto> orderList = orderService.getAllOrders();
		modelAndView.addObject("orderList", orderList);

		return modelAndView;
	}
	
	@PostMapping("/delete/{id}")
	public String deleteOrder(@PathVariable UUID id, RedirectAttributes attributes) {
		HashMap<String, String> result = orderService.deleteOrder(id);
		attributes.addFlashAttribute("status", result.get("status"));
		attributes.addFlashAttribute("message", result.get("message"));
		
		return "redirect:/order/list";
		
		
	}
	
	@GetMapping("/edit/{id}")
	public ModelAndView editOrderView(@PathVariable String id, EditOrderDto editOrderDto) {
		UUID uuid = UUID.fromString(id);
		ModelAndView modelAndView = super.view("order/edit");
		modelAndView.addObject("dto", orderService.getOrderById(uuid));
		modelAndView.addObject("partList", orderPartService.findAllByOrderId(uuid));
		modelAndView.addObject("repairList", orderRepairService.findAllByOrderId(uuid));
		modelAndView.addObject("parts", partService.getAllPartsByDeletedAtNull());
		modelAndView.addObject("repairs", repairService.getAllServicesByDeletedAtNull());
		return modelAndView;
		
	}
	
	@PostMapping("/edit/{id}")
	public String editOrder(@PathVariable String id,
	                        @Valid EditOrderDto editOrderDto,
	                        BindingResult bindingResult,
	                        RedirectAttributes redirectAttributes) throws AccessDeniedException {
		UUID uuid = UUID.fromString(id);
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("editOrderDto", editOrderDto);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editOrderDto", bindingResult);
			return "redirect:/order/edit/" + id;
		}
		HashMap<String, String> result = orderService.editOrder(uuid, editOrderDto);
		redirectAttributes.addFlashAttribute("status", result.get("status"));
		redirectAttributes.addFlashAttribute("message", result.get("message"));
		return "redirect:/order/list";
	}
}
