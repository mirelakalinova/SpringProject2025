package com.example.mkalinova.app.client.controller;


import ch.qos.logback.core.model.Model;
import com.example.mkalinova.app.Land.Controller.BaseController;
import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.dto.CarDtoEditClient;
import com.example.mkalinova.app.car.service.CarService;
import com.example.mkalinova.app.client.data.dto.AddClientDto;
import com.example.mkalinova.app.client.data.dto.EditClientDto;
import com.example.mkalinova.app.client.service.ClientService;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.dto.CompanyDtoEditClient;
import com.example.mkalinova.app.company.service.CompanyService;
import com.example.mkalinova.app.util.ObjectUtils;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/client")
public class ClientController extends BaseController {

    private final CarService carService;
    private final CompanyService companyService;
    private final ClientService clientService;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public ClientController(CarService carService, CompanyService companyService, ClientService clientService, ModelMapper mapper, Validator validator) {
        this.carService = carService;
        this.companyService = companyService;
        this.clientService = clientService;
        this.modelMapper = mapper;
        this.validator = validator;
    }


    @ModelAttribute("addClientDto")
    public AddClientDto addClientDto() {
        return new AddClientDto();
    }

    @ModelAttribute("addCarDto")
    public AddCarDto addCarDto() {
        return new AddCarDto();
    }


    @ModelAttribute("addCompanyDto")
    public AddCompanyDto addCompanyDto() {
        return new AddCompanyDto();
    }


    @GetMapping("/clients")
    public ModelAndView clientList() {
        ModelAndView modelAndView = super.view("/client/clients");
        modelAndView.addObject("clients", clientService.getAllWithCarsAndCompanies());
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addClient() {

        ModelAndView model = super.view("/client/add");

        model.addObject("companies", companyService.allCompaniesWithoutClient());
        List<AddCarDto> cars = carService.allCarsWithoutUser();
        model.addObject("cars", cars);
        model.addObject("formType", "client");
        return model;


    }

    @PostMapping("/add")
    public String createUser(@Valid  AddClientDto addClientDto,
                             BindingResult bindingResult,
                             @Valid   AddCarDto addCarDto,
                             BindingResult bindingResultAddCarDto,
                             @Valid   AddCompanyDto addCompanyDto,
                             BindingResult bindingResultAddCompanyDto,
                             RedirectAttributes attributes) throws AccessDeniedException {

        boolean validateClient = false;
        boolean validateCar = false;
        boolean validateCompany = false;
        boolean isCarDtoEmpty = ObjectUtils.isAllFieldsNullOrEmpty(addCarDto);

        Map<String, Object> validationResults = new HashMap<>();
        validateClient = validateDto(addClientDto, bindingResult, attributes, "addClientDto");
        validationResults.put("addClientDto", validateClient);
        // boolean carIsEmpty = validator.validate(addCarDto).isEmpty();
        if (!isCarDtoEmpty) {
            validateCar = validateDto(addCarDto, bindingResultAddCarDto, attributes, "addCarDto");
            validationResults.put("car", validateCar);
        }
        if (addCompanyDto.isShowForm()) {
            validateCompany = validateDto(addCompanyDto, bindingResultAddCompanyDto, attributes, "addCompanyDto");
            validationResults.put("company", validateCompany);
        }


        // Ако има грешки -> редирект
        if (validationResults.containsValue(false)) {
            addFlashAttributes(addClientDto, bindingResult, addCarDto, bindingResultAddCarDto, addCompanyDto, bindingResultAddCompanyDto, attributes, isCarDtoEmpty);
            return "redirect:/client/add";

        }
        HashMap<String, String> result = clientService.addClientWithAdditionalData(addClientDto, addCarDto, addCompanyDto, addCompanyDto.isShowForm());
        // Ако някой от методите в контролера е върнал статус error, редирект към адд
        if (result.get("status").equals("error")) {
            attributes.addFlashAttribute("addClientDto", addClientDto);
            if (addCarDto != null) {

                attributes.addFlashAttribute("addCarDto", addCarDto);
            }
            if (addCompanyDto.isShowForm()) {

                attributes.addFlashAttribute("addCompanyDto", addCompanyDto);
                attributes.addFlashAttribute("showDiv", "true");
            }
            attributes.addFlashAttribute("status", result.get("status"));
            attributes.addFlashAttribute("message", result.get("message"));
            return "redirect:/client/add";

        }
        attributes.addFlashAttribute("message", result.get("message"));
        attributes.addFlashAttribute("status", "success");
        return "redirect:/client/clients";


    }


    private static void addFlashAttributes(AddClientDto addClientDto, BindingResult bindingResult, AddCarDto addCarDto, BindingResult bindingResultAddCarDto, AddCompanyDto addCompanyDto, BindingResult bindingResultAddCompanyDto, RedirectAttributes attributes, boolean isCarDtoEmpty) {
        attributes.addFlashAttribute("addClientDto", addClientDto);
        attributes.addFlashAttribute("org.springframework.validation.BindingResult.addClientDto", bindingResult);

        if (!isCarDtoEmpty) {

            attributes.addFlashAttribute("addCarDto", addCarDto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.addCarDto", bindingResultAddCarDto);
        }

        if (addCompanyDto.isShowForm()) {

            attributes.addFlashAttribute("addCompanyDto", addCompanyDto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.addCompanyDto", bindingResultAddCompanyDto);
        }
    }


    private <T> boolean validateDto(T dto, BindingResult bindingResult, RedirectAttributes attributes, String dtoName) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute(dtoName, dto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult." + dtoName, bindingResult);
            return false;
        }
        return true;
    }



    //todo -> add dto
    @PostMapping("/delete/{id}")
    public String deleteClientWithAllData(@PathVariable Long id, RedirectAttributes attributes) throws AccessDeniedException {

        clientService.deleteClient(id);

        attributes.addFlashAttribute("message", "Успещно изтрит клиент #" + id);
        attributes.addFlashAttribute("status", "success");

        return "redirect:/client/clients";
    }


    @GetMapping("/edit/{id}")
    public ModelAndView editClient(@PathVariable Long id) {
        ModelAndView modelAndView = super.view("/client/edit");
        modelAndView.addObject("companiesWithoutUser", companyService.allCompaniesWithoutClient());
        modelAndView.addObject("carsWithoutUser",  carService.allCarsWithoutUser(CarDtoEditClient.class));
        EditClientDto client = clientService.findClientById(id);

        List<CarDto> cars = clientService.getCarsByClient(id);
        modelAndView.addObject("client", client);
        modelAndView.addObject("cars", client.getCars());
        modelAndView.addObject("clientId", client.getId());
        modelAndView.addObject("companies", client.getCompanies());

        return modelAndView;

    }

    @PutMapping("/edit/{id}")
    public String updateClient(@PathVariable Long id,
                               @Valid EditClientDto editClientDto,
                               @Valid CompanyDtoEditClient companyDtoEditClient,
                               BindingResult bindingResultCar,
                               BindingResult bindingResult,
                               RedirectAttributes attributes

    ) throws AccessDeniedException {

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("editClientDto", editClientDto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.editClientDto", bindingResult);

            return "redirect:/client/edit/{id}";
        }
        HashMap<String, String> result = clientService.updateClient(id, editClientDto);

        attributes.addFlashAttribute("message", result.get("message") );
        attributes.addFlashAttribute("status", result.get("status") );
        return "redirect:/client/edit/{id}";
    }

    @PostMapping("/remove-car/{id}")
    public String removeCarFromClient(@PathVariable Long id,   @RequestParam("clientId")Long clientId , RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = clientService.removeCar(id, clientId);
        Model model = new Model();
        attributes.addFlashAttribute("message", result.get("message") );
        attributes.addFlashAttribute("status", result.get("status") );
        return "redirect:/client/edit/" + clientId;

    }

    @PostMapping("/remove-company/{id}")
    public String removeCompanyFromClient(@PathVariable Long id,   @RequestParam("clientId")Long clientId , RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = clientService.removeCompany(id, clientId);
        attributes.addFlashAttribute("status", result.get("status"));
        attributes.addFlashAttribute("message", result.get("message"));
        return "redirect:/client/edit/" + clientId;


    }
}



