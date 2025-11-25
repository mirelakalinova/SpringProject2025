package com.example.mkalinova.app.company.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.client.data.dto.ClientDto;
import com.example.mkalinova.app.client.data.dto.ClientListCarDto;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.dto.EditCompanyDto;
import com.example.mkalinova.app.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    public final CompanyService companyService;
    public final ClientService clientService;

    public CompanyController(CompanyService companyService, ClientService clientService) {
        this.companyService = companyService;
        this.clientService = clientService;
    }

    @ModelAttribute("addCompanyDto")
    public AddCompanyDto addCompanyDto() {
        return new AddCompanyDto();
    }


    //todo IT
    @GetMapping("/add")
    public ModelAndView addCompany() {
        ModelAndView modelAndView = super.view("company/add");
        modelAndView.addObject("clients", clientService.findAll(ClientListCarDto.class));

        return modelAndView;
    }

    @PostMapping("/add")
    public String addNewCompany(@Valid AddCompanyDto addCompanyDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) throws AccessDeniedException {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addCompanyDto", addCompanyDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addCompanyDto", bindingResult);
            return "redirect:/company/add";
        }

        companyService.saveCompany(addCompanyDto);
        return "redirect:/company/list";
    }

    @GetMapping("/list")
    public ModelAndView companyList() {
        ModelAndView modelAndView = super.view("company/list");
        modelAndView.addObject("companies", companyService.getAllActiveCompanies());

        return modelAndView;
    }
    //todo IT
    @GetMapping("/edit/{id}")
    public ModelAndView editCompany(@PathVariable String id, Model model) {
        UUID uuid = UUID.fromString(id);
        ModelAndView modelAndView = super.view("company/edit");
        EditCompanyDto editCompanyDto = (EditCompanyDto) companyService.getById(uuid, EditCompanyDto.class);
        ClientDto client = companyService.getCompanyClient(uuid);
        modelAndView.addObject("existingClient", client);

        modelAndView.addObject("clients", clientService.getAllWithCarsAndCompanies());
        if (!model.containsAttribute("editCompanyDto")) {

            modelAndView.addObject("editCompanyDto", editCompanyDto);
        }

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editCompany(@Valid EditCompanyDto editCompanyDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes
                            ) throws AccessDeniedException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("editCompanyDto", editCompanyDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editCompanyDto", bindingResult);
            return "redirect:/company/edit/" + editCompanyDto.getId();
        }
        boolean isClientPresent = editCompanyDto.getClientId() != null;
        companyService.updateCompany(editCompanyDto, isClientPresent ,editCompanyDto.getClientId());
        return "redirect:/company/list";
    }

    @PostMapping("/remove-client/{id}")
    public String removeClient(@PathVariable String id, @RequestParam("companyId") String companyId,
                               RedirectAttributes attributes) throws AccessDeniedException {
        UUID uuid = UUID.fromString(id);
        UUID uuidCompany = UUID.fromString(companyId);
        HashMap<String, String> result = companyService.removeClient(uuid, uuidCompany);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/company/list";


    }

    @PostMapping("delete/{id}")
    public String deleteCompany(@PathVariable String id, RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = companyService.deleteCompany(id);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/company/list";

    }
}
