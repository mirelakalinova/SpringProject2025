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
@RequestMapping("/repair")
public class RepairController extends BaseController {
    private final RepairService service;

    public RepairController(RepairService service) {
        this.service = service;
    }


    @GetMapping("/list")
    public ModelAndView listService() {
        ModelAndView modelAndView = super.view("repair/list");

        modelAndView.addObject("repairs", service.getAllServicesByDeletedAtNull());
        return modelAndView;
    }


    @GetMapping("/add")
    public ModelAndView addRepair(@ModelAttribute RepairDto repairDto) {
        return super.view("repair/add");
    }

    @PostMapping("/add")
    public String addNewRepair(@Valid RepairDto repairDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(repairDto.getPrice() == null){
            repairDto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("repairDto", repairDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.repairDto", bindingResult);
            return "redirect:/repair/add";
        }
        HashMap<String, String> result =  service.addService(repairDto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/repair/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editView(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = super.view("repair/edit");
        modelAndView.addObject("repairDto", service.findById(id));

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editRepair(@Valid EditRepairDto repairDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(repairDto.getPrice() == null){
            repairDto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("repairDto", repairDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.repairDto", bindingResult);
            return "redirect:/repair/edit/" + repairDto.getId();
        }
        HashMap<String,String> result = service.editService(repairDto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/repair/list";
    }


    @PostMapping("/delete/{id}")
    public String deleteRepair(@PathVariable Long id, RepairDto repairDto,
                             RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = service.deleteService(id);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/repair/list";

    }
}
