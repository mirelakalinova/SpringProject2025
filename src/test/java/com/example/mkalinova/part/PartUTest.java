package com.example.mkalinova.part;

import com.example.mkalinova.app.parts.data.dto.EditPartDto;
import com.example.mkalinova.app.parts.data.dto.PartDto;
import com.example.mkalinova.app.parts.data.dto.PartListDto;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.parts.repo.PartRepository;
import com.example.mkalinova.app.parts.service.PartServiceImpl;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class PartUTest {
    @Mock
    private PartRepository partRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PartServiceImpl service;
    private User admin;
    private User editor;
    private Part partFirst;
    private Part deletedPart;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        partRepository.deleteAll();
        partFirst = new Part();
        partFirst.setPrice(120);
        partFirst.setName("test");

        partRepository.saveAndFlush(partFirst);

        deletedPart = new Part();
        deletedPart.setPrice(125);
        deletedPart.setName("testSecond");
        deletedPart.setDeletedAt(LocalDateTime.now());

        partRepository.saveAndFlush(deletedPart);
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
    void getAllActiveParts_ReturnListOfOne() {
        when(modelMapper.map(partFirst, PartListDto.class)).thenReturn(new PartListDto());
        when(partRepository.findAllByDeletedAtNull()).thenReturn(List.of(partFirst));
        List<PartListDto> result = service.getAllPartsByDeletedAtNull();
        verify(partRepository).findAllByDeletedAtNull();
        assertEquals(1, result.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewPartWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        PartDto dto = new PartDto();
        dto.setName("TestDto");
        dto.setPrice(120D);
        Part part = new Part();
        part.setName(dto.getName());
        part.setPrice(dto.getPrice());

        when(partRepository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(part));
        when(modelMapper.map(dto, Part.class)).thenReturn(part);

        HashMap<String, String> result = service.addPart(dto);
        verify(partRepository).findByName(dto.getName());
        verify(partRepository).save(part);
        assertEquals("success", result.get("status"));
        Optional<Part> optPart = partRepository.findByName(part.getName());
        assertTrue(optPart.isPresent());
        assertEquals(120, optPart.get().getPrice());


    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addNewPartWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        PartDto dto = new PartDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Part part = new Part();
        part.setName(dto.getName());
        part.setPrice(0);

        when(partRepository.findByName(dto.getName())).thenReturn(Optional.empty()).thenReturn(Optional.of(part));
        when(modelMapper.map(dto, Part.class)).thenReturn(part);

        HashMap<String, String> result = service.addPart(dto);
        verify(partRepository).findByName(dto.getName());
        verify(partRepository).save(part);
        assertEquals("success", result.get("status"));
        Optional<Part> optPart = partRepository.findByName(part.getName());
        assertTrue(optPart.isPresent());
        assertEquals(0, optPart.get().getPrice());

    }

    @Test
    @WithAnonymousUser
    void addNewPartWithPrice_AccessDenied() throws AccessDeniedException {

        PartDto dto = new PartDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Part part = new Part();
        part.setName(dto.getName());
        part.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> service.addPart(dto));
        verify(partRepository, times(0)).findByName(dto.getName());
        verify(partRepository, times(0)).save(part);


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editPartWithoutPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        partFirst.setId(UUID.randomUUID());
        partRepository.save(partFirst);
        EditPartDto dto = new EditPartDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        dto.setId(partFirst.getId());


        when(partRepository.findById(dto.getId())).thenReturn(Optional.of(partFirst));


        HashMap<String, String> result = service.editPart(dto);
        verify(partRepository).findById(dto.getId());
        verify(partRepository, times(2)).save(partFirst);
        assertEquals("success", result.get("status"));
        Optional<Part> optPart = partRepository.findById(dto.getId());
        assertTrue(optPart.isPresent());
        assertEquals(0, optPart.get().getPrice());
        assertEquals(dto.getName(), optPart.get().getName());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editPartWithPrice_Success() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        partFirst.setId(UUID.randomUUID());
        partRepository.save(partFirst);
        EditPartDto dto = new EditPartDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(partFirst.getId());


        when(partRepository.findById(dto.getId())).thenReturn(Optional.of(partFirst));


        HashMap<String, String> result = service.editPart(dto);
        verify(partRepository).findById(dto.getId());
        verify(partRepository, times(2)).save(partFirst);
        assertEquals("success", result.get("status"));
        Optional<Part> optPart = partRepository.findById(dto.getId());
        assertTrue(optPart.isPresent());
        assertEquals(130, optPart.get().getPrice());
        assertEquals(dto.getName(), optPart.get().getName());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editPart_ErrorMessage() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();

        EditPartDto dto = new EditPartDto();
        dto.setName("TestDto");
        dto.setPrice(130D);
        dto.setId(UUID.randomUUID());
        when(partRepository.findById(dto.getId())).thenReturn(Optional.empty());
        HashMap<String, String> result = service.editPart(dto);
        verify(partRepository).findById(dto.getId());
        verify(partRepository, never()).save(partFirst);
        assertEquals("error", result.get("status"));
        Optional<Part> optPart = partRepository.findByName(dto.getName());
        assertFalse(optPart.isPresent());


    }


    @Test
    @WithAnonymousUser
    void editPartWithPrice_AccessDenied() throws AccessDeniedException {

        PartDto dto = new PartDto();
        dto.setName("TestDto");
        dto.setPrice(null);
        Part part = new Part();
        part.setName(dto.getName());
        part.setPrice(120D);

        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> service.addPart(dto));
        verify(partRepository, times(0)).findByName(dto.getName());
        verify(partRepository, times(0)).save(part);


    }


    @Test
    @WithAnonymousUser
    void deletePartByAnonymous_AccessDenied() throws AccessDeniedException {


        doThrow(new AccessDeniedException("Нямате права да извършите тази операция!")).when(userService).isUserLogIn();
        assertThrows(AccessDeniedException.class, () -> service.deletePart(partFirst.getId()));
        verify(partRepository, times(0)).findById(partFirst.getId());
        verify(partRepository, times(0)).save(partFirst);
        assertNull(partFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    void deletePartByEditor_AccessDenied() throws AccessDeniedException {
        doNothing().when(userService).isUserLogIn();
        when(userService.getLoggedInUser()).thenReturn(Optional.of(editor));
        when(userService.isAdmin(editor)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> service.deletePart(partFirst.getId()));
        verify(partRepository, times(0)).findById(partFirst.getId());
        verify(partRepository, times(0)).save(partFirst);
        assertNull(partFirst.getDeletedAt());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePartByAdmin_AccessDenied() throws AccessDeniedException {
        partFirst.setId(UUID.randomUUID());
        partRepository.save(partFirst);
        UUID id = partFirst.getId();

        doNothing().when(userService).isUserLogIn();

        when(userService.getLoggedInUser()).thenReturn(Optional.of(admin));
        when(userService.isAdmin(admin)).thenReturn(true);
        when(partRepository.findById(id)).thenReturn(Optional.of(partFirst));

        HashMap<String, String> result = service.deletePart(partFirst.getId());

        verify(partRepository, times(1)).findById(partFirst.getId());
        verify(partRepository, times(2)).save(partFirst);

        assertNotEquals(null, partFirst.getDeletedAt());


    }
}
