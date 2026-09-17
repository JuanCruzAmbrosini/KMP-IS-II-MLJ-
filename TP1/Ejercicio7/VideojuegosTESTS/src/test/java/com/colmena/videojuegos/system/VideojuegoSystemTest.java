package com.colmena.videojuegos.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TIPO DE PRUEBA: PRUEBAS DEL SISTEMA (SYSTEM TESTING / E2E)
 * Objetivo: Validación completa del sistema de software integrado de extremo a extremo,
 * verificando el enrutamiento HTTP, resolución de controladores, inyección de modelos
 * y renderizado de plantillas de vista.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VideojuegoSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("System Test: La ruta raíz '/' responde HTTP 200 y renderiza la vista 'index'")
    void testRootEndpoint_RespondeOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("saludo"));
    }

    @Test
    @DisplayName("System Test: La ruta '/inicio' responde HTTP 200 y carga el catalogo de videojuegos")
    void testInicioEndpoint_CargaCatalogo() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("views/inicio"))
                .andExpect(model().attributeExists("videojuegos"));
    }

    @Test
    @DisplayName("System Test: La ruta '/crud' responde HTTP 200 y expone la lista para administracion")
    void testCrudEndpoint_ExponeGestion() throws Exception {
        mockMvc.perform(get("/crud"))
                .andExpect(status().isOk())
                .andExpect(view().name("views/crud"))
                .andExpect(model().attributeExists("videojuegos"));
    }

    @Test
    @DisplayName("System Test: La ruta '/busqueda' con query de busqueda responde correctamente")
    void testBusquedaEndpoint_ConQuery() throws Exception {
        mockMvc.perform(get("/busqueda").param("query", "Mario"))
                .andExpect(status().isOk())
                .andExpect(view().name("views/busqueda"))
                .andExpect(model().attributeExists("videojuegos"));
    }
}

