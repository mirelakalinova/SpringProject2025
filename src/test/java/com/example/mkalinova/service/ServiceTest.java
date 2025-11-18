package com.example.mkalinova.service;


import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.carService.data.dto.CarServiceDto;
import com.example.mkalinova.app.carService.data.dto.CarServiceListDto;
import com.example.mkalinova.app.carService.data.dto.EditCarServiceDto;
import com.example.mkalinova.app.carService.data.entity.CarService;
import com.example.mkalinova.app.carService.repo.CarServiceRepository;
import com.example.mkalinova.app.carService.service.CarServiceServiceImpl;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ServiceTest {
    @Mock
    private CarServiceRepository serviceRepository;
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
    private CarService serviceFirst;
    private CarService deletedService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        serviceRepository.deleteAll();
        serviceFirst = new CarService();
        serviceFirst.setPrice(120);
        serviceFirst.setName("test");

        serviceRepository.saveAndFlush(serviceFirst);

        deletedService = new CarService();
        deletedService.setPrice(125);
        deletedService.setName("testSecond");
        deletedService.setDeletedAt(LocalDateTime.now());

        serviceRepository.saveAndFlush(deletedService);
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
        when(modelMapper.map(serviceFirst, CarServiceListDto.class)).thenReturn(new CarServiceListDto());
        when(serviceRepository.findAllByDeletedAtNull()).thenReturn(List.of(serviceFirst));
        List<CarServiceListDto> result = service.getAllServicesByDeletedAtNull();
        verify(serviceRepository).findAllByDeletedAtNull();
        assertEquals(1, result.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewserviceWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        CarServiceDto dto = new CarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(120D);
        CarService service = new CarService();
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());

        when(serviceRepository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(service));
        when(modelMapper.map(dto, CarService.class)).thenReturn(service);

        HashMap<String, String> result = this.service.addService(dto);
        verify(serviceRepository).findByName(dto.getName());
        verify(serviceRepository).save(service);
        assertEquals("success", result.get("status"));
        Optional<CarService> optService = serviceRepository.findByName(service.getName());
        assertTrue(optService.isPresent());
        assertEquals(120, optService.get().getPrice());


    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewServiceWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        CarServiceDto dto = new CarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        CarService service = new CarService();
        service.setName(dto.getName());
        service.setPrice(0);

        when(serviceRepository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(service));
        when(modelMapper.map(dto, CarService.class)).thenReturn(service);

        HashMap<String, String> result = this.service.addService(dto);
        verify(serviceRepository).findByName(dto.getName());
        verify(serviceRepository).save(service);
        assertEquals("success", result.get("status"));
        Optional<CarService> optService = serviceRepository.findByName(service.getName());
        assertTrue(optService.isPresent());
        assertEquals(0, optService.get().getPrice());

    }

    @Test
    @WithAnonymousUser
    void addNewserviceWithPrice_AccessDenied() throws AccessDeniedException {

        CarServiceDto dto = new CarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        CarService service = new CarService();
        service.setName(dto.getName());
        service.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.addService(dto));
        verify(serviceRepository, times(0)).findByName(dto.getName());
        verify(serviceRepository, times(0)).save(service);


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editServiceWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        serviceFirst.setId(3L);
        serviceRepository.save(serviceFirst);
        EditCarServiceDto dto = new EditCarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        dto.setId(serviceFirst.getId());


        when(serviceRepository.findById(dto.getId())).thenReturn(Optional.of(serviceFirst));


        HashMap<String, String> result = this.service.editService(dto);
        verify(serviceRepository).findById(dto.getId());
        verify(serviceRepository, times(2)).save(serviceFirst);
        assertEquals("success", result.get("status"));
        Optional<CarService> optService = serviceRepository.findById(dto.getId());
        assertTrue(optService.isPresent());
        assertEquals(0, optService.get().getPrice());
        assertEquals(dto.getName(), optService.get().getName());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editServiceWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        serviceFirst.setId(3L);
        serviceRepository.save(serviceFirst);
        EditCarServiceDto dto = new EditCarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(serviceFirst.getId());


        when(serviceRepository.findById(dto.getId())).thenReturn(Optional.of(serviceFirst));


        HashMap<String, String> result = this.service.editService(dto);
        verify(serviceRepository).findById(dto.getId());
        verify(serviceRepository, times(2)).save(serviceFirst);
        assertEquals("success", result.get("status"));
        Optional<CarService> optService = serviceRepository.findById(dto.getId());
        assertTrue(optService.isPresent());
        assertEquals(130, optService.get().getPrice());
        assertEquals(dto.getName(), optService.get().getName());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editService_ErrorMessage() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();

        EditCarServiceDto dto = new EditCarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(1L);
        when(serviceRepository.findById(dto.getId())).thenReturn(Optional.empty());
        HashMap<String, String> result = this.service.editService(dto);
        verify(serviceRepository).findById(dto.getId());
        verify(serviceRepository, never()).save(serviceFirst);
        assertEquals("error", result.get("status"));
        Optional<CarService> optService = serviceRepository.findByName(dto.getName());
        assertFalse(optService.isPresent());


    }


    @Test
    @WithAnonymousUser
    void editServiceWithPrice_AccessDenied() throws AccessDeniedException {

        EditCarServiceDto dto = new EditCarServiceDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        CarService service = new CarService();
        service.setName(dto.getName());
        service.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.editService(dto));
        verify(serviceRepository, times(0)).findByName(dto.getName());
        verify(serviceRepository, times(0)).save(service);


    }


    @Test
    @WithAnonymousUser
    void deleteServiceByAnonymous_AccessDenied() throws AccessDeniedException {


        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> this.service.deleteService(serviceFirst.getId()));
        verify(serviceRepository, times(0)).findById(serviceFirst.getId());
        verify(serviceRepository, times(0)).save(serviceFirst);
        assertEquals(null, serviceFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    void deleteServiceByEditor_AccessDenied() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        when(userService.getLoggedInUser()).thenReturn(Optional.of(editor));
        when(userService.isAdmin(editor)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> this.service.deleteService(serviceFirst.getId()));
        verify(serviceRepository, times(0)).findById(serviceFirst.getId());
        verify(serviceRepository, times(0)).save(serviceFirst);
        assertEquals(null, serviceFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteserviceByAdmin_AccessDenied() throws AccessDeniedException {
        serviceFirst.setId(1L);
        serviceRepository.save(serviceFirst);
        Long id = serviceFirst.getId();

        doNothing().when(userService).isUserLogIn();

        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(userService.isAdmin(admin)).thenReturn(true);
        when(serviceRepository.findById(id)).thenReturn(Optional.of(serviceFirst));

        HashMap<String, String> result = this.service.deleteService(serviceFirst.getId());

        verify(serviceRepository, times(1)).findById(serviceFirst.getId());
        verify(serviceRepository, times(2)).save(serviceFirst);

        assertNotEquals(null, serviceFirst.getDeletedAt());


    }
}
