package com.example.mkalinova.service;

import com.example.mkalinova.app.repair.data.dto.RepairListDto;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
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
    private RepairRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    private User admin;
    private User editor;
    private Repair repairFirst;
    private Repair repairSecond;



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

        repairFirst = new Repair();
        repairFirst.setName("Test first service");
        repairFirst.setPrice(120D);
        repairSecond = new Repair();
        repairSecond.setPrice(140D);
        repairFirst.setName("Test second service");
        repairSecond.setDeletedAt(LocalDateTime.now());
        repository.save(repairFirst);
        repository.save(repairSecond);
    }

    @Test
    public void getServiceList() throws Exception {
        ArrayList<RepairListDto> list = new ArrayList<>();
        list.add(modelMapper.map(repairFirst, RepairListDto.class));
        list.add(modelMapper.map(repairSecond, RepairListDto.class));
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

        Optional<Repair> repair = repository.findByName("Test123");
        assertTrue(repair.isPresent());
        assertEquals(120D, repair.get().getPrice());

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

        Optional<Repair> service = repository.findByName("Test");
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

        Optional<Repair> service = repository.findByName("Test");
        assertFalse(service.isPresent());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void editServiceByAdmin_Success() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .param("name", "Test")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/list"));

        Optional<Repair> repair = repository.findByName("Test");
        assertNotNull(repair);
        assertEquals("Test", repair.get().getName());
        assertEquals(110D, repair.get().getPrice());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void editServiceByAdmin_Error() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .param("name", "Te")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/edit/" + repairFirst.getId()));

        Optional<Repair> service = repository.findByName("Test");
        assertTrue(service.isEmpty());

    }

    @Test
    @WithAnonymousUser
    public void editServiceByAnonymous_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/edit/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .param("name", "Test")
                        .param("price", String.valueOf(110D))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<Repair> repair = repository.findByName("Test");
        assertTrue(repair.isEmpty());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void DeleteServiceByAdmin_Success() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/service/list"));

        Optional<Repair> repair = repository.findById(repairFirst.getId());
        assertTrue(repair.get().getDeletedAt() != null);


    }


    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void DeleteCarServiceByEditor_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<Repair> repair = repository.findById(repairFirst.getId());
        assertTrue(repair.get().getDeletedAt() == null);


    }


    @Test
    @WithAnonymousUser
    public void DeleteserviceByAnonymous_AccessDenied() throws Exception {
        mockMvc.perform(post("/service/delete/{id}", repairFirst.getId())
                        .param("id", String.valueOf(repairFirst.getId()))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<Repair> repair = repository.findById(repairFirst.getId());
        assertTrue(repair.get().getDeletedAt() == null);


    }
}
