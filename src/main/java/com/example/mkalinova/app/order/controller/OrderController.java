package com.example.mkalinova.app.order.controller;

import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.dto.OrderListDto;
import com.example.mkalinova.app.order.service.OrderService;
import com.example.mkalinova.app.parts.service.PartService;
import com.example.mkalinova.app.repair.service.RepairService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.BeanInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {

    private final CarService carService;
    private final CompanyService companyService;
    private final ClientService clientService;
    private final PartService partService;
    private final RepairService repairService;
    private final OrderService orderService;

    public OrderController(CarService carService, CompanyService companyService, ClientService clientService, PartService partService, RepairService repairService, OrderService orderService) {
        this.carService = carService;
        this.companyService = companyService;
        this.clientService = clientService;
        this.partService = partService;
        this.repairService = repairService;
        this.orderService = orderService;
    }

    @GetMapping("/add")
    public ModelAndView addOrder(Model model) throws JsonProcessingException {

        ModelAndView modelAndView = super.view("order/add");
        modelAndView.addObject("parts", partService.getAllPartsByDeletedAtNull());
        modelAndView.addObject("repairs", repairService.getAllServicesByDeletedAtNull());
        if (!model.containsAttribute("orderDto")){
            modelAndView.addObject("orderDto", new AddOrderDto());
        }
        AddOrderDto dto = (AddOrderDto) model.asMap().get("orderDto");
        if(dto != null){
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
                              RedirectAttributes redirectAttributes) {

        System.out.println();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("orderDto", orderDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderDto", bindingResult);
            return "redirect:/order/add";
        }

        HashMap<String, String > result = orderService.saveOrder(orderDto);
        if( result.get("status").equals("error")){
            redirectAttributes.addFlashAttribute("orderDto", orderDto);
        }
        return "redirect:/order/add";

    }

    @GetMapping("/list")
    private ModelAndView orderList(){
        ModelAndView modelAndView = super.view("order/list");
        List<OrderListDto> orderList = orderService.getAllOrders();
        modelAndView.addObject("orderList", orderList);
        System.out.println();
        return modelAndView;
    }
}
