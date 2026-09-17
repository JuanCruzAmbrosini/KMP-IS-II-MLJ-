package com.colmena.videojuegos.nonfunctional;

import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TIPO DE PRUEBA: PRUEBAS NO FUNCIONALES
 * Objetivo: Evaluación de aspectos operativos y de robustez del sistema ante fallos,
 * manejo controlado de excepciones y resiliencia ante solicitudes anómalas o recursos inexistentes.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VideojuegosNonFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Test
    @DisplayName("No Funcional - Robustez: Solicitud de detalle con ID inexistente maneja excepcion y deriva a vista de error")
    void testRobustez_IdInexistenteRetornaVistaError() throws Exception {
        mockMvc.perform(get("/detalle/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("No Funcional - Resiliencia del Servicio: Consulta de ID nulo o invalido lanza excepcion controlada")
    void testResilienciaServicio_IdInvalido() {
        assertThrows(Exception.class, () -> {
            servicioVideojuego.findById(-1L);
        }, "El servicio debe lanzar una excepción controlada ante IDs inválidos");
    }

    @Test
    @DisplayName("No Funcional - Manejo de Peticiones: Busqueda vacia no corrompe el contexto y responde normalmente")
    void testEstabilidad_BusquedaConQueryVacio() throws Exception {
        mockMvc.perform(get("/busqueda").param("query", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("views/busqueda"));
    }
}

