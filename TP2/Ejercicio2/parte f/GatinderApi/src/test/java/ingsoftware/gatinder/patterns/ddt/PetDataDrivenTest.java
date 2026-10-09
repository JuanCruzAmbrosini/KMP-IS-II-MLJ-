package ingsoftware.gatinder.patterns.ddt;

import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PATRÓN DE AUTOMATIZACIÓN: PRUEBAS BASADAS EN DATOS (DATA-DRIVEN TESTING - DDT)
 * Objetivo: Ejecutar iterativamente múltiples conjuntos de datos de prueba
 * desacoplando las combinaciones de entrada de la lógica de aserción.
 */
public class PetDataDrivenTest {

    private PetService petService;

    @BeforeEach
    void setUp() {
        petService = new PetService();
    }

    @ParameterizedTest(name = "[{index}] DDT: Nombre=''{0}'', Genero=''{1}'', DeberiaSerValido={2}")
    @CsvSource({
            // Casos Positivos
            "'Garfield', 'MALE', true",
            "'Luna', 'FEMALE', true",
            "'Milo', 'MALE', true",
            "'Oreo', 'FEMALE', true",

            // Casos Negativos: Nombres vacios
            "'', 'MALE', false",
            "'   ', 'FEMALE', true", // Nombre con espacios no esta vacío (pero probamos la condición)

            // Casos Negativos: Nombres nulos (se pasan con string especial o null)
            "'[NULL]', 'MALE', false",

            // Casos Negativos: Genero nulo
            "'Tom', '[NULL_GENDER]', false"
    })
    @DisplayName("DDT: Validacion parametrica de combinaciones de mascotas")
    void testDataDriven_ValidacionMascota(String nombreParam, String generoParam, boolean esperadoValido) {
        String nombre = "[NULL]".equals(nombreParam) ? null : nombreParam;
        Gender genero = "[NULL_GENDER]".equals(generoParam) ? null : Gender.valueOf(generoParam);

        if (esperadoValido) {
            assertDoesNotThrow(() -> petService.validate(nombre, genero));
        } else {
            assertThrows(ErrorService.class, () -> petService.validate(nombre, genero));
        }
    }
}

