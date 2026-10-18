package ingsoftware.gatinder.unit;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.repository.PetRepository;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;
import ingsoftware.gatinder.service.PictureService;
import ingsoftware.gatinder.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TIPO DE PRUEBA: PRUEBA UNITARIA (UNIT TESTING)
 * Objetivo: Verificar el funcionamiento aislado de las reglas de negocio de PetService
 * mediante la simulación de repositorios y servicios dependientes con Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class PetServiceUnitTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserService userService;

    @Mock
    private PictureService pictureService;

    @InjectMocks
    private PetService petService;

    private User userMock;
    private Pet petMock;

    @BeforeEach
    void setUp() {
        userMock = new User();
        userMock.setId("user-123");
        userMock.setFirstName("Juan");
        userMock.setLastName("Pérez");

        petMock = new Pet();
        petMock.setId("pet-456");
        petMock.setName("Michi");
        petMock.setGender(Gender.MALE);
        petMock.setAnimal(Animal.CAT);
        petMock.setUser(userMock);
        petMock.setDeleted(false);
    }

    @Test
    @DisplayName("Unit Test: Crear mascota con datos validos se guarda exitosamente")
    void testCreatePet_ConDatosValidos_GuardaExitosamente() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cat.jpg", "image/jpeg", new byte[]{1, 2, 3});
        Picture pictureMock = new Picture();
        pictureMock.setId("pic-789");

        when(userService.findById("user-123")).thenReturn(userMock);
        when(pictureService.create(file)).thenReturn(pictureMock);
        when(petRepository.save(any(Pet.class))).thenReturn(petMock);

        assertDoesNotThrow(() -> {
            petService.create(file, "user-123", "Michi", Gender.MALE, Animal.CAT);
        });

        verify(userService, times(1)).findById("user-123");
        verify(pictureService, times(1)).create(file);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Unit Test: Crear mascota con nombre nulo lanza ErrorService")
    void testCreatePet_NombreNulo_LanzaErrorService() {
        MockMultipartFile file = new MockMultipartFile("file", "cat.jpg", "image/jpeg", new byte[]{1});

        ErrorService excepcion = assertThrows(ErrorService.class, () -> {
            petService.create(file, "user-123", null, Gender.MALE, Animal.CAT);
        });

        assertEquals("El nombre de la mascota no puede ser nulo o vacío", excepcion.getMessage());
        verifyNoInteractions(petRepository);
    }

    @Test
    @DisplayName("Unit Test: Baja logica de mascota exitosa cuando el solicitante es el dueno")
    void testDeletePet_PermitidoParaElDueno() throws Exception {
        when(petRepository.findById("pet-456")).thenReturn(Optional.of(petMock));

        petService.delete("pet-456", "user-123");

        assertTrue(petMock.isDeleted(), "La mascota debe quedar marcada con deleted=true");
        verify(petRepository, times(1)).save(petMock);
    }

    @Test
    @DisplayName("Unit Test: Baja logica rechazada si el usuario no es el dueno legitimo")
    void testDeletePet_RechazadoParaUsuarioAjeno() {
        when(petRepository.findById("pet-456")).thenReturn(Optional.of(petMock));

        ErrorService excepcion = assertThrows(ErrorService.class, () -> {
            petService.delete("pet-456", "usuario-intruso");
        });

        assertEquals("No tiene permiso para eliminar esta mascota", excepcion.getMessage());
        assertFalse(petMock.isDeleted());
        verify(petRepository, never()).save(any(Pet.class));
    }
}
