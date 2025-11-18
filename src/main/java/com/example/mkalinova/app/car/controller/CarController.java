package com.example.mkalinova.app.car.controller;

import com.example.mkalinova.app.land.Controller.BaseController;
import com.example.mkalinova.app.car.data.dto.AddCarDto;

import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.car.data.dto.EditCarDto;
import com.example.mkalinova.app.car.service.CarService;

import com.example.mkalinova.app.client.data.dto.ClientListCarDto;
import com.example.mkalinova.app.client.service.ClientService;
import jakarta.validation.Valid;
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
@RequestMapping("/car")
public class CarController extends BaseController {
    public final CarService carService;
    public final ClientService clientService;
    public CarController(CarService carService, ClientService clientService) {
        this.carService = carService;
        this.clientService = clientService;
    }

    @ModelAttribute("addCarDto")
    public AddCarDto addCarDto() {
        return new AddCarDto();
    }




    @GetMapping("/add")
    public ModelAndView addCar() {

        ModelAndView modelAndView = super.view("car/add-car");
//        modelAndView.addObject("formType", "car");
        modelAndView.addObject("clients", clientService.findAll(ClientListCarDto.class));
//        modelAndView.addObject("heading", "add");

        return modelAndView;
    }

    @PostMapping("/add")
    public String addCarToRepo(@Valid AddCarDto addCarDto,
                               BindingResult bindingResult
                              , RedirectAttributes redirectAttributes
                               ) throws AccessDeniedException {
        ModelAndView modelAndView = new ModelAndView();
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("addCarDto", addCarDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addCarDto", bindingResult);
            return "redirect:/car/add";
        }
        Map<String, String> result = carService.addCarAndReturnMessage(addCarDto);
        String status = result.get("status");
        redirectAttributes.addFlashAttribute("status", status);
        redirectAttributes.addFlashAttribute("message", result.get("message"));
        if(!status.equals("success")){
            return "redirect:/car/add";
        } else {
            return "redirect:/car/cars";
        }
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editCar(@PathVariable Long id) {

        ModelAndView modelAndView = super.view("car/edit");
        modelAndView.addObject("editCarDto", carService.findById(id, EditCarDto.class));
        modelAndView.addObject("clients", clientService.findAll(ClientListCarDto.class));

        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public String editCar(@Valid EditCarDto editCarDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes)
    {
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("editCarDto", editCarDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editCarDto",bindingResult);
            return "redirect:/car/edit/" + editCarDto.getId();
        }
        HashMap<String, String> result = carService.editCar(editCarDto.getId(), editCarDto);
        return "redirect:/car/cars";
    }


    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id,  RedirectAttributes attributes) throws AccessDeniedException {
        HashMap<String, String> result = carService.deleteCarById(id);
        attributes.addFlashAttribute("message", result.get("message"));
        attributes.addFlashAttribute("status", result.get("status"));
        return "redirect:/car/cars";

    }
    @GetMapping("cars")
    public ModelAndView carList(){
        ModelAndView modelAndView = super.view("car/cars");

        List<CarDto> carList = carService.getAll();
        modelAndView.addObject("cars", carList);

        return modelAndView;
    }
}
