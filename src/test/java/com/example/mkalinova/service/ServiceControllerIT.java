package com.example.mkalinova.service;

import com.example.mkalinova.app.carService.data.dto.CarServiceListDto;
import com.example.mkalinova.app.carService.data.entity.CarService;
import com.example.mkalinova.app.carService.repo.CarServiceRepository;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarServiceRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    private User admin;
    private User editor;
    private CarService serviceFirst;
    private CarService serviceSecond;



    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        repository.deleteAll();
        admin = new User();
        admin.setFirstName("Mirela");
        admin.setLastName("Kalinova");
        admin.setUsername("admin");
        admin.setEmail("admin@test.bg");
        admin.setPassword("Password1234!");
        admin.setRole(UsersRole.ADMIN);

        userRepository.save(admin);


        editor = new User();
        editor.setFirstName("editor");
        editor.setLastName("editor");
        editor.setUsername("editor");
        editor.setEmail("editor@test.bg");
        editor.setPassword("Password1234!");
        editor.setRole(UsersRole.EDITOR);
        userRepository.save(editor);

        serviceFirst = new CarService();
        serviceFirst.setName("Test first service");
        serviceFirst.setPrice(120D);
        serviceSecond = new CarService();
        serviceSecond.setPrice(140D);
        serviceFirst.setName("Test second service");
        serviceSecond.setDeletedAt(LocalDateTime.now());
        repository.save(serviceFirst);
        repository.save(serviceSecond);
    }

    @Test
    public void getServiceList() throws Exception {
        ArrayList<CarServiceListDto> list = new ArrayList<>();
        list.add(modelMapper.map(serviceFirst, CarServiceListDto.class));
        list.add(modelMapper.map(serviceSecond, CarServiceListDto.class));
        mockMvc.perform(get("/service/list").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attribute("services", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addservice_Success() throws Exception {
        mockMvc.perform(post("/service/add")
                        .param("name", "Test123")
                        .param("price", String.valueOf(120d))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/list"));

        Optional<CarService> service = repository.findByName("Test123");
        assertTrue(service.isPresent());
        assertEquals(120D, service.get().getPrice());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addService_Error() throws Exception {
        mockMvc.perform(post("/service/add")
                        .param("name", "Te")
                        .param("price", String.valueOf(120d))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/add"));

        Optional<CarService> service = repository.findByName("Test");
        assertFalse(service.isPresent());

    }

    @Test
    @WithAnonymousUser
    public void addService_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/add")
                        .param("name", "Test")
                        .param("price", String.valueOf(120d))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<CarService> service = repository.findByName("Test");
        assertFalse(service.isPresent());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void editServiceByAdmin_Success() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .param("name", "Test")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/list"));

        Optional<CarService> service = repository.findByName("Test");
        assertNotNull(service);
        assertEquals("Test", service.get().getName());
        assertEquals(110D, service.get().getPrice());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void editServiceByAdmin_Error() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .param("name", "Te")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/edit/" + serviceFirst.getId()));

        Optional<CarService> service = repository.findByName("Test");
        assertTrue(service.isEmpty());

    }

    @Test
    @WithAnonymousUser
    public void editServiceByAnonymous_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .param("name", "Test")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<CarService> service = repository.findByName("Test");
        assertTrue(service.isEmpty());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void DeleteServiceByAdmin_Success() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/list"));

        Optional<CarService> service = repository.findById(serviceFirst.getId());
        assertTrue(service.get().getDeletedAt() != null);


    }


    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void DeleteCarServiceByEditor_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<CarService> service = repository.findById(serviceFirst.getId());
        assertTrue(service.get().getDeletedAt() == null);


    }


    @Test
    @WithAnonymousUser
    public void DeleteserviceByAnonymous_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", serviceFirst.getId())
                        .param("id", String.valueOf(serviceFirst.getId()))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<CarService> service = repository.findById(serviceFirst.getId());
        assertTrue(service.get().getDeletedAt() == null);


    }
}
