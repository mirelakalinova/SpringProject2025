package com.example.mkalinova.app.carService.controller;

import com.example.mkalinova.app.Land.Controller.BaseController;
import com.example.mkalinova.app.carService.data.dto.CarServiceDto;
import com.example.mkalinova.app.carService.data.dto.EditCarServiceDto;
import com.example.mkalinova.app.carService.service.CarServiceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;

@Controller
@RequestMapping("/service")
public class CarServiceController extends BaseController {
    private final CarServiceService carService;

    public CarServiceController(CarServiceService carService) {
        this.carService = carService;
    }


    @GetMapping("/list")
    public ModelAndView listService() {
        ModelAndView modelAndView = super.view("service/list");

        modelAndView.addObject("services", carService.getAllServicesByDeletedAtNull());
        return modelAndView;
    }


    @GetMapping("/add")
    public ModelAndView addService(@ModelAttribute CarServiceDto dto) {
        return super.view("service/add");
    }

    @PostMapping("/add")
    public String addNewService(@Valid CarServiceDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(dto.getPrice() == null){
            dto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("dto", dto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.dto", bindingResult);
            return "redirect:/service/add";
        }
        HashMap<String, String> result =  carService.addService(dto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editView(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = super.view("service/edit");
        modelAndView.addObject("dto", carService.findById(id));

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editService(@Valid EditCarServiceDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(dto.getPrice() == null){
            dto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("dto", dto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.dto", bindingResult);
            return "redirect:/service/edit/" + dto.getId();
        }
        HashMap<String,String> result = carService.editService(dto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";
    }


    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id, CarServiceDto dto,
                             RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = carService.deleteService(id);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";

    }
}
