package com.example.gatinder.service;

import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.repository.UserRepository;
import ingsoftware.gatinder.service.ZoneService;
import ingsoftware.gatinder.service.PictureService;
import ingsoftware.gatinder.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ZoneService zoneService;

    @Mock
    private PictureService pictureService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegistrar() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setFirstName("leandro");
        dto.setLastName("spadaro");
        dto.setEmail("leandro@mail.com");
        dto.setPassword("1234567");
        dto.setRepeatPassword("1234567");
        dto.setZoneId("1");

        Zone zoneMock = new Zone();
        when(zoneService.findById("1")).thenReturn(zoneMock);
        when(pictureService.create(any())).thenReturn(new Picture());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.create(null, dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPassword(),
                dto.getRepeatPassword(), dto.getZoneId());

        verify(zoneService, times(1)).findById("1");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testModificar() throws Exception {
        Zone zoneMock = new Zone();
        User original = new User();
        original.setId("1");
        original.setPicture(new Picture());

        when(zoneService.findById("1")).thenReturn(zoneMock);
        when(userRepository.findById("1")).thenReturn(Optional.of(original));
        when(pictureService.update(any(), any())).thenReturn(new Picture());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.update(null, "1", "leandro2", "spadaro", "leandro@mail.com", "1234567", "1234567", "1");

        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(original);
    }

    @Test
    public void testModificarInexistente() throws Exception {
        when(zoneService.findById("1")).thenReturn(new Zone());
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        ErrorService exception = assertThrows(ErrorService.class, () -> {
            userService.update(null, "123", "leandro", "spadaro", "leandro@mail.com", "1234567", "1234567", "1");
        });

        assertEquals("No se encontró el usuario con el ID proporcionado", exception.getMessage());
    }

}
