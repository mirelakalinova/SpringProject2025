package com.example.mkalinova.repair;


import com.example.mkalinova.app.repair.data.dto.RepairDto;
import com.example.mkalinova.app.repair.data.dto.EditRepairDto;
import com.example.mkalinova.app.repair.data.dto.RepairListDto;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repair.repo.RepairRepository;
import com.example.mkalinova.app.repair.service.CarServiceServiceImpl;

import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class RepairUTest {
    @Mock
    private RepairRepository repository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CarServiceServiceImpl service;
    private User admin;
    private User editor;
    private Repair repairFirst;
    private Repair deletedService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        repository.deleteAll();
        repairFirst = new Repair();
        repairFirst.setPrice(120);
        repairFirst.setName("test");

        repository.saveAndFlush(repairFirst);

        deletedService = new Repair();
        deletedService.setPrice(125);
        deletedService.setName("testSecond");
        deletedService.setDeletedAt(LocalDateTime.now());

        repository.saveAndFlush(deletedService);
        admin = new User();
        admin.setFirstName("Mirela");
        admin.setLastName("Kalinova");
        admin.setUsername("admin");
        admin.setEmail("admin@test.bg");
        admin.setPassword("Password1234!");
        admin.setRole(UsersRole.ADMIN);

        editor = new User();
        editor.setFirstName("Mirela");
        editor.setLastName("Kalinova");
        editor.setUsername("editor");
        editor.setEmail("editor@test.bg");
        editor.setPassword("Password1234!");
        editor.setRole(UsersRole.EDITOR);
        userRepository.saveAndFlush(admin);
        userRepository.saveAndFlush(editor);
    }

    @Test
    void getAllActiveServices_ReturnListOfOne() {
        when(modelMapper.map(repairFirst, RepairListDto.class)).thenReturn(new RepairListDto());
        when(repository.findAllByDeletedAtNull()).thenReturn(List.of(repairFirst));
        List<RepairListDto> result = service.getAllServicesByDeletedAtNull();
        verify(repository).findAllByDeletedAtNull();
        assertEquals(1, result.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewserviceWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        RepairDto dto = new RepairDto();
        dto.setName("TestDto");
        dto.setPrice(120D);
        Repair repair = new Repair();
        repair.setName(dto.getName());
        repair.setPrice(dto.getPrice());

        when(repository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(repair));
        when(modelMapper.map(dto, Repair.class)).thenReturn(repair);

        HashMap<String, String> result = this.service.addService(dto);
        verify(repository).findByName(dto.getName());
        verify(repository).save(repair);
        assertEquals("success", result.get("status"));
        Optional<Repair> optService = repository.findByName(repair.getName());
        assertTrue(optService.isPresent());
        assertEquals(120, optService.get().getPrice());


    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewServiceWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        RepairDto dto = new RepairDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Repair service = new Repair();
        service.setName(dto.getName());
        service.setPrice(0);

        when(repository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(service));
        when(modelMapper.map(dto, Repair.class)).thenReturn(service);

        HashMap<String, String> result = this.service.addService(dto);
        verify(repository).findByName(dto.getName());
        verify(repository).save(service);
        assertEquals("success", result.get("status"));
        Optional<Repair> optRepair = repository.findByName(service.getName());
        assertTrue(optRepair.isPresent());
        assertEquals(0, optRepair.get().getPrice());

    }

    @Test
    @WithAnonymousUser
    void addNewserviceWithPrice_AccessDenied() throws AccessDeniedException {

        RepairDto dto = new RepairDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Repair repair = new Repair();
        repair.setName(dto.getName());
        repair.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.addService(dto));
        verify(repository, times(0)).findByName(dto.getName());
        verify(repository, times(0)).save(repair);


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editServiceWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        repairFirst.setId(UUID.randomUUID());
        repository.save(repairFirst);
        EditRepairDto dto = new EditRepairDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        dto.setId(repairFirst.getId());


        when(repository.findById(dto.getId())).thenReturn(Optional.of(repairFirst));


        HashMap<String, String> result = this.service.editService(dto);
        verify(repository).findById(dto.getId());
        verify(repository, times(2)).save(repairFirst);
        assertEquals("success", result.get("status"));
        Optional<Repair> optService = repository.findById(dto.getId());
        assertTrue(optService.isPresent());
        assertEquals(0, optService.get().getPrice());
        assertEquals(dto.getName(), optService.get().getName());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editServiceWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        repairFirst.setId(UUID.randomUUID());
        repository.save(repairFirst);
        EditRepairDto dto = new EditRepairDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(repairFirst.getId());


        when(repository.findById(dto.getId())).thenReturn(Optional.of(repairFirst));


        HashMap<String, String> result = this.service.editService(dto);
        verify(repository).findById(dto.getId());
        verify(repository, times(2)).save(repairFirst);
        assertEquals("success", result.get("status"));
        Optional<Repair> optService = repository.findById(dto.getId());
        assertTrue(optService.isPresent());
        assertEquals(130, optService.get().getPrice());
        assertEquals(dto.getName(), optService.get().getName());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editService_ErrorMessage() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();

        EditRepairDto dto = new EditRepairDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(UUID.randomUUID());
        when(repository.findById(dto.getId())).thenReturn(Optional.empty());
        HashMap<String, String> result = this.service.editService(dto);
        verify(repository).findById(dto.getId());
        verify(repository, never()).save(repairFirst);
        assertEquals("error", result.get("status"));
        Optional<Repair> optService = repository.findByName(dto.getName());
        assertFalse(optService.isPresent());


    }


    @Test
    @WithAnonymousUser
    void editServiceWithPrice_AccessDenied() throws AccessDeniedException {

        EditRepairDto dto = new EditRepairDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Repair repair = new Repair();
        repair.setName(dto.getName());
        repair.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.editService(dto));
        verify(repository, times(0)).findByName(dto.getName());
        verify(repository, times(0)).save(repair);


    }


    @Test
    @WithAnonymousUser
    void deleteServiceByAnonymous_AccessDenied() throws AccessDeniedException {


        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.deleteService(repairFirst.getId()));
        verify(repository, times(0)).findById(repairFirst.getId());
        verify(repository, times(0)).save(repairFirst);
        assertEquals(null, repairFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    void deleteServiceByEditor_AccessDenied() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        when(userService.getLoggedInUser()).thenReturn(Optional.of(editor));
        when(userService.isAdmin(editor)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> this.service.deleteService(repairFirst.getId()));
        verify(repository, times(0)).findById(repairFirst.getId());
        verify(repository, times(0)).save(repairFirst);
        assertEquals(null, repairFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteserviceByAdmin_AccessDenied() throws AccessDeniedException {
        repairFirst.setId(UUID.randomUUID());
        repository.save(repairFirst);
        UUID id = repairFirst.getId();

        doNothing().when(userService).isUserLogIn();

        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(userService.isAdmin(admin)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(repairFirst));

        HashMap<String, String> result = this.service.deleteService(repairFirst.getId());

        verify(repository, times(1)).findById(repairFirst.getId());
        verify(repository, times(2)).save(repairFirst);

        assertNotEquals(null, repairFirst.getDeletedAt());


    }
}
