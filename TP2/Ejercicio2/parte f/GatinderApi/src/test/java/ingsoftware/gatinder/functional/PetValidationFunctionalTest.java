package ingsoftware.gatinder.functional;

import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBAS FUNCIONALES
 * Objetivo: Evaluar el cumplimiento de las reglas funcionales y validaciones
 * de entrada de datos del negocio (integridad de nombres, géneros y restricciones).
 */
public class PetValidationFunctionalTest {

    private PetService petService;

    @BeforeEach
    void setUp() {
        petService = new PetService();
    }

    @Test
    @DisplayName("Funcional: Nombre valido y genero valido pasan la validacion sin errores")
    void testValidacionExitosa() {
        assertDoesNotThrow(() -> {
            petService.validate("Milo", Gender.MALE);
        });
    }

    @Test
    @DisplayName("Funcional: Nombre nulo debe arrojar ErrorService")
    void testValidacionNombreNulo() {
        ErrorService ex = assertThrows(ErrorService.class, () -> {
            petService.validate(null, Gender.MALE);
        });
        assertEquals("El nombre de la mascota no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Funcional: Nombre en blanco debe arrojar ErrorService")
    void testValidacionNombreVacio() {
        ErrorService ex = assertThrows(ErrorService.class, () -> {
            petService.validate("", Gender.FEMALE);
        });
        assertEquals("El nombre de la mascota no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Funcional: Genero nulo debe arrojar ErrorService")
    void testValidacionGeneroNulo() {
        ErrorService ex = assertThrows(ErrorService.class, () -> {
            petService.validate("Luna", null);
        });
        assertEquals("El género de la mascota no puede ser nulo", ex.getMessage());
    }
}

