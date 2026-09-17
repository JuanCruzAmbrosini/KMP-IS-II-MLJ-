package com.colmena.videojuegos.patterns.pom;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PATRÓN DE DISEÑO DE AUTOMATIZACIÓN: MODELO DE OBJETO DE PÁGINAS (PAGE OBJECT MODEL - POM)
 * Objetivo: Abstraer la representación de las páginas web (vistas HTML/Thymeleaf) en una clase Java.
 * Encapsula los selectores, rutas y acciones del usuario (navegación, envío de formularios, verificación),
 * desacoplando la lógica de la prueba de los detalles estructurales de la interfaz de usuario.
 */
public class VideojuegoCrudPage {

    private final MockMvc mockMvc;
    private final String urlCrud = "/crud";
    private final String urlFormulario = "/formulario/videojuego/0";

    public VideojuegoCrudPage(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Acción: Navegar al panel de administración CRUD.
     */
    public MvcResult navegarAlCrud() throws Exception {
        return mockMvc.perform(get(urlCrud))
                .andExpect(status().isOk())
                .andExpect(view().name("views/crud"))
                .andReturn();
    }

    /**
     * Acción: Cargar el formulario de alta de nuevo videojuego.
     */
    public MvcResult abrirFormularioNuevoVideojuego() throws Exception {
        return mockMvc.perform(get(urlFormulario))
                .andExpect(status().isOk())
                .andExpect(view().name("views/formulario/videojuego"))
                .andExpect(model().attributeExists("videojuego"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("estudios"))
                .andReturn();
    }

    /**
     * Acción: Buscar un videojuego por término de búsqueda.
     */
    public MvcResult buscarVideojuego(String termino) throws Exception {
        return mockMvc.perform(get("/busqueda").param("query", termino))
                .andExpect(status().isOk())
                .andExpect(view().name("views/busqueda"))
                .andReturn();
    }
}

