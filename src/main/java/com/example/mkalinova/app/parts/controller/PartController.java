package com.example.mkalinova.app.parts.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.parts.data.dto.EditPartDto;
import com.example.mkalinova.app.parts.data.dto.PartDto;
import com.example.mkalinova.app.parts.service.PartService;
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
@RequestMapping("/part")
public class PartController extends BaseController {
    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }


    @GetMapping("/list")
    public ModelAndView listPart() {
        ModelAndView modelAndView = super.view("part/list");

        modelAndView.addObject("parts", partService.getAllPartsByDeletedAtNull());
        return modelAndView;
    }


    @GetMapping("/add")
    public ModelAndView addPart(@ModelAttribute PartDto partDto) {
        return super.view("part/add");
    }

    @PostMapping("/add")
    public String addNewPart(@Valid PartDto partDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(partDto.getPrice() == null){
            partDto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("partDto", partDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.partDto", bindingResult);
            return "redirect:/part/add";
        }
        HashMap<String, String> result =  partService.addPart(partDto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/part/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editView(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = super.view("part/edit");
        modelAndView.addObject("partDto", partService.findById(id));

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editPart(@Valid EditPartDto partDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AccessDeniedException {
        if(partDto.getPrice() == null){
            partDto.setPrice(0D);
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("partDto", partDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.partDto", bindingResult);
            return "redirect:/part/edit/" + partDto.getId();
        }
        HashMap<String,String> result = partService.editPart(partDto);
        redirectAttributes.addFlashAttribute("status", result.get("status"));
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/part/list";
    }


    @PostMapping("/delete/{id}")
    public String deletePart(@PathVariable Long id, PartDto partDto,
                             RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = partService.deletePart(id);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/part/list";

    }
}
