package com.example.gatinder.service;

import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.repository.PetRepository;
import ingsoftware.gatinder.repository.PetAuditRepository;
import ingsoftware.gatinder.service.PictureService;
import ingsoftware.gatinder.service.UserService;
import ingsoftware.gatinder.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserService userService;

    @Mock
    private PictureService pictureService;

    @Mock
    private PetAuditRepository petAuditRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAgregarMascota() throws Exception {
        User userMock = new User();
        userMock.setId("123");

        when(userService.findById("123")).thenReturn(userMock);
        when(petRepository.save(any(Pet.class))).thenReturn(new Pet());

        petService.create(null, "123", "choco", Gender.MALE, Animal.DOG);

        verify(userService, times(1)).findById("123");
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    public void testAgregarMascotaExcepcion() throws Exception {
        when(userService.findById("123")).thenThrow(new ErrorService("Usuario no encontrado"));

        ErrorService exception = assertThrows(ErrorService.class, () -> {
            petService.create(null, "123", "romi", Gender.FEMALE, Animal.CAT);
        });

        assertNotNull(exception);
    }

    @Test
    public void testActualizarMascota() throws Exception {
        User user = new User();
        user.setId("123");

        Pet originalPet = new Pet();
        originalPet.setId("1");
        originalPet.setUser(user);
        originalPet.setDeleted(false);

        when(petRepository.findById("1")).thenReturn(Optional.of(originalPet));
        when(petRepository.save(any(Pet.class))).thenReturn(new Pet());

        petService.update(null, "1", "123", "actualizado", Gender.MALE, Animal.DOG);

        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    public void testActualizarMascotaExceptionNoExisteMascota() {
        when(petRepository.findById("1")).thenReturn(Optional.empty());

        ErrorService exception = assertThrows(ErrorService.class, () -> {
            petService.update(null, "1", "123", "actualizar", Gender.MALE, Animal.DOG);
        });

        assertEquals("No se encontró la mascota con el ID proporcionado", exception.getMessage());
    }

    @Test
    public void testActualizarMascotaExceptionUsuario() {
        User realUser = new User();
        realUser.setId("real");

        Pet originalPet = new Pet();
        originalPet.setId("1");
        originalPet.setUser(realUser);

        when(petRepository.findById("1")).thenReturn(Optional.of(originalPet));

        ErrorService exception = assertThrows(ErrorService.class, () -> {
            petService.update(null, "1", "falso", "nose", Gender.FEMALE, Animal.CAT);
        });

        assertEquals("No tiene permiso para modificar esta mascota", exception.getMessage());
    }

    @Test
    public void testEliminarMascota() throws Exception {
        User user = new User();
        user.setId("123");

        Pet originalPet = new Pet();
        originalPet.setId("1");
        originalPet.setUser(user);

        when(petRepository.findById("1")).thenReturn(Optional.of(originalPet));

        petService.delete("1", "123");

        verify(petRepository, times(1)).save(any(Pet.class));
        assertTrue(originalPet.isDeleted());
    }
}