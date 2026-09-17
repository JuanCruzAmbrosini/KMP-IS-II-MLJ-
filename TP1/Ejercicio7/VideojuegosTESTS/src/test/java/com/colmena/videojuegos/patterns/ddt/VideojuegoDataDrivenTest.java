package com.colmena.videojuegos.patterns.ddt;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PATRÓN DE AUTOMATIZACIÓN: PRUEBAS BASADAS EN DATOS (DATA-DRIVEN TESTING - DDT)
 * Objetivo: Separar la lógica de ejecución del script de prueba de los conjuntos de datos de entrada.
 * Se ejecutan iterativamente múltiples escenarios (casos positivos, casos límite y negativos)
 * reutilizando una única estructura lógica de validación mediante @ParameterizedTest y @CsvSource.
 */
public class VideojuegoDataDrivenTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "[{index}] DDT: Titulo=''{0}'', Precio={1}, Stock={2}, Descripcion=''{3}'', DeberiaSerValido={4}")
    @CsvSource({
            // Casos Positivos y Limites
            "'God of War', 59.99, 10, 'Aventura epica nordica', true",
            "'Halo Infinite', 5.00, 1, 'Master Chief en accion', true",      // Limites minimos validos (precio 5, stock 1)
            "'Elden Ring', 9999.00, 9999, 'Mundo abierto fantastico', true", // Limites maximos validos (precio <10000, stock <10000)

            // Casos Negativos: Precio fuera de rango
            "'Minecraft', 4.99, 10, 'Construccion con bloques', false",       // Precio menor a 5
            "'Cyberpunk', 10001.00, 5, 'Distopia futurista en Night City', false", // Precio excede 10000

            // Casos Negativos: Stock invalido
            "'Super Mario', 50.00, 0, 'Plataformas clasico', false",          // Stock menor a 1

            // Casos Negativos: Descripcion demasiado corta (< 5 caracteres)
            "'Tetris', 10.00, 5, 'Test', false"                              // Descripcion solo 4 caracteres
    })
    @DisplayName("DDT: Validacion parametrica de combinaciones de atributos en Videojuego")
    void testDataDriven_ValidacionCombinaciones(String titulo, float precio, short stock, String descripcion, boolean esperadoValido) {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("General");

        Estudio est = new Estudio();
        est.setId(1L);
        est.setNombre("Estudio");

        Videojuego v = new Videojuego();
        v.setTitulo(titulo);
        v.setPrecio(precio);
        v.setStock(stock);
        v.setDescripcion(descripcion);
        v.setFechaLanzamiento(new Date());
        v.setActivo(true);
        v.setCategoria(cat);
        v.setEstudio(est);

        Set<ConstraintViolation<Videojuego>> violations = validator.validate(v);
        boolean esValido = violations.isEmpty();

        assertEquals(esperadoValido, esValido,
                String.format("Fallo para: titulo=%s, precio=%.2f, stock=%d. Esperado valido: %b, pero fue: %b",
                        titulo, precio, stock, esperadoValido, esValido));
    }
}

