package com.example.mkalinova.app.apiService.controller;

import com.example.mkalinova.app.apiService.data.dto.MakeDto;
import com.example.mkalinova.app.apiService.data.dto.ModelDto;
import com.example.mkalinova.app.apiService.service.ApiService;
import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.land.Controller.BaseController;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("api")
public class MakeModelController extends BaseController {
	private final ApiService apiService;
	
	public MakeModelController(ApiService apiService) {
		this.apiService = apiService;
	}
	
	@GetMapping("/models")
	public ModelAndView allModels(@PageableDefault(page = 0, size = 30) Pageable pageable) {
		Page<ModelDto> page = apiService.getModelsPage(pageable);
		ModelAndView mv = super.view("api/model-list");
		mv.addObject("page", page);
		mv.addObject("models", page.getContent());
		return mv;
	}
	
	
	@PostMapping("/delete/model/{id}")
	public String deleteModel(@PathVariable String id,
	                          RedirectAttributes attributes) throws AccessDeniedException {
		UUID uuid = UUID.fromString(id);
		HashMap<String, String> result;
		try {
			result = apiService.deleteModel(uuid);
			attributes.addFlashAttribute("status", result.get("status"));
			attributes.addFlashAttribute("message", result.get("message"));
			return "redirect:/api/models";
		} catch (FeignException.NotFound e) {
			throw new NoSuchResourceException("Моделът с id " + id + " не съществува!.");
		} catch (FeignException e) {
			throw new RuntimeException("Грешка при изтриване на модела: " + e.getMessage());
		}
	}
	
	@GetMapping("/makes")
	public ModelAndView allMakes(@PageableDefault(page = 0, size = 30) Pageable pageable) {
		Page<MakeDto> page = apiService.getMakesPage(pageable);
		ModelAndView mv = super.view("api/make-list");
		mv.addObject("page", page);
		mv.addObject("makes", page.getContent());
		return mv;
	}
	
	
	@PostMapping("/delete/make/{id}")
	public String deleteMake(@PathVariable String id,
	                         RedirectAttributes attributes) throws AccessDeniedException {
		UUID uuid = UUID.fromString(id);
		HashMap<String, String> result;
		try {
			result = apiService.deleteMake(uuid);
			attributes.addFlashAttribute("status", result.get("status"));
			attributes.addFlashAttribute("message", result.get("message"));
			return "redirect:/api/makes";
		} catch (FeignException.NotFound e) {
			throw new NoSuchResourceException("Марка с id " + id + " не съществува!.");
		} catch (FeignException e) {
			throw new RuntimeException("Грешка при изтриване на марка: " + e.getMessage());
		}
	}
}
