package com.colmena.videojuegos.patterns.pom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PATRÓN DE AUTOMATIZACIÓN: PRUEBA BASADA EN PAGE OBJECT MODEL (POM)
 * Objetivo: Demostrar cómo los casos de prueba interactúan con la clase Page Object
 * sin conocer los detalles de rutas directas o selectores de la página web.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VideojuegosPomTest {

    @Autowired
    private MockMvc mockMvc;

    private VideojuegoCrudPage crudPage;

    @BeforeEach
    void setUp() {
        crudPage = new VideojuegoCrudPage(mockMvc);
    }

    @Test
    @DisplayName("POM Test: Interaccion con el panel CRUD a traves del Page Object")
    void testPom_NavegacionCrud() throws Exception {
        MvcResult resultado = crudPage.navegarAlCrud();
        assertNotNull(resultado.getResponse());
        assertTrue(resultado.getResponse().getContentAsString().contains("Videojuegos") || resultado.getModelAndView() != null);
    }

    @Test
    @DisplayName("POM Test: Apertura de formulario de alta a traves del Page Object")
    void testPom_AperturaFormularioAlta() throws Exception {
        MvcResult resultado = crudPage.abrirFormularioNuevoVideojuego();
        assertNotNull(resultado.getModelAndView().getModel().get("videojuego"));
    }

    @Test
    @DisplayName("POM Test: Realizar busqueda interactuando con el Page Object")
    void testPom_BusquedaJuego() throws Exception {
        MvcResult resultado = crudPage.buscarVideojuego("Pokemon");
        assertNotNull(resultado.getResponse());
    }
}

