package com.example.mkalinova.app.repair.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.repair.data.dto.RepairDto;
import com.example.mkalinova.app.repair.data.dto.EditRepairDto;
import com.example.mkalinova.app.repair.service.RepairService;
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
public class RepairController extends BaseController {
    private final RepairService service;

    public RepairController(RepairService service) {
        this.service = service;
    }


    @GetMapping("/list")
    public ModelAndView listService() {
        ModelAndView modelAndView = super.view("service/list");

        modelAndView.addObject("services", service.getAllServicesByDeletedAtNull());
        return modelAndView;
    }


    @GetMapping("/add")
    public ModelAndView addService(@ModelAttribute RepairDto dto) {
        return super.view("service/add");
    }

    @PostMapping("/add")
    public String addNewService(@Valid RepairDto dto,
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
        HashMap<String, String> result =  service.addService(dto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editView(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = super.view("service/edit");
        modelAndView.addObject("dto", service.findById(id));

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editService(@Valid EditRepairDto dto,
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
        HashMap<String,String> result = service.editService(dto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";
    }


    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id, RepairDto dto,
                             RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = service.deleteService(id);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/service/list";

    }
}
