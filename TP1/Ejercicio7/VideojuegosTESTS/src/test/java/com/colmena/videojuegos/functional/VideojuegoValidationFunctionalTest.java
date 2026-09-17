package com.colmena.videojuegos.functional;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBAS FUNCIONALES
 * Objetivo: Evaluación rigurosa de las reglas de negocio y restricciones funcionales
 * aplicadas al modelo de dominio (validaciones de campos, rangos de precio, stock y fechas).
 */
public class VideojuegoValidationFunctionalTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Videojuego crearVideojuegoValido() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Accion");

        Estudio est = new Estudio();
        est.setId(1L);
        est.setNombre("Sony");

        Videojuego v = new Videojuego();
        v.setTitulo("God of War Ragnarok");
        v.setDescripcion("Aventura mitologica nordica con Kratos y Atreus");
        v.setPrecio(69.99f);
        v.setStock((short) 10);
        v.setFechaLanzamiento(new Date());
        v.setActivo(true);
        v.setCategoria(cat);
        v.setEstudio(est);
        return v;
    }

    @Test
    @DisplayName("Funcional: Videojuego con todos los campos validos pasa sin violaciones")
    void testVideojuegoValido_SinViolaciones() {
        Videojuego vj = crearVideojuegoValido();
        Set<ConstraintViolation<Videojuego>> violations = validator.validate(vj);
        assertTrue(violations.isEmpty(), "No deben existir violaciones de restricciones en un objeto valido");
    }

    @Test
    @DisplayName("Funcional: Precio menor a 5 debe disparar violacion de regla de negocio Min")
    void testPrecioMenorAlMinimo_FallaValidacion() {
        Videojuego vj = crearVideojuegoValido();
        vj.setPrecio(2.0f); // Minimo requerido es 5

        Set<ConstraintViolation<Videojuego>> violations = validator.validate(vj);
        assertFalse(violations.isEmpty(), "Debe fallar por precio menor al minimo permitido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precio")));
    }

    @Test
    @DisplayName("Funcional: Stock menor a 1 debe disparar violacion de regla de negocio Min")
    void testStockInvalido_FallaValidacion() {
        Videojuego vj = crearVideojuegoValido();
        vj.setStock((short) 0); // Minimo requerido es 1

        Set<ConstraintViolation<Videojuego>> violations = validator.validate(vj);
        assertFalse(violations.isEmpty(), "Debe fallar por stock menor a 1");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stock")));
    }

    @Test
    @DisplayName("Funcional: Fecha de lanzamiento en el futuro debe fallar por restriccion PastOrPresent")
    void testFechaFutura_FallaValidacion() {
        Videojuego vj = crearVideojuegoValido();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 2);
        vj.setFechaLanzamiento(cal.getTime()); // Fecha futura

        Set<ConstraintViolation<Videojuego>> violations = validator.validate(vj);
        assertFalse(violations.isEmpty(), "Una fecha de lanzamiento futura debe ser rechazada");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaLanzamiento")));
    }

    @Test
    @DisplayName("Funcional: Descripcion demasiado corta (menor a 5 caracteres) falla validacion")
    void testDescripcionCorta_FallaValidacion() {
        Videojuego vj = crearVideojuegoValido();
        vj.setDescripcion("ABC"); // Minimo 5 caracteres

        Set<ConstraintViolation<Videojuego>> violations = validator.validate(vj);
        assertFalse(violations.isEmpty(), "Descripcion menor a 5 caracteres debe generar violacion");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
    }
}

