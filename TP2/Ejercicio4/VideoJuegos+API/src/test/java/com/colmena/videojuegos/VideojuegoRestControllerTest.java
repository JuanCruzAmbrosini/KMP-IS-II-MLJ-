package com.colmena.videojuegos;

import com.colmena.videojuegos.controllers.VideojuegoRestController;
import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideojuegoRestController.class)
class VideojuegoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioVideojuego servicioVideojuego;

    @Test
    void testGetAllTemplateMethod() throws Exception {
        Videojuego v1 = new Videojuego();
        v1.setId(1L);
        v1.setTitulo("Super Mario Bros");

        when(servicioVideojuego.findAll()).thenReturn(Arrays.asList(v1));

        mockMvc.perform(get("/api/videojuegos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Super Mario Bros"));

        verify(servicioVideojuego).findAll();
    }

    @Test
    void testGetByIdTemplateMethod() throws Exception {
        Videojuego v1 = new Videojuego();
        v1.setId(1L);
        v1.setTitulo("The Legend of Zelda");

        when(servicioVideojuego.findById(1L)).thenReturn(Optional.of(v1));

        mockMvc.perform(get("/api/videojuegos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("The Legend of Zelda"));

        verify(servicioVideojuego).findById(1L);
    }

    @Test
    void testGetByIdNotFoundTemplateMethod() throws Exception {
        when(servicioVideojuego.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/videojuegos/99"))
                .andExpect(status().isNotFound());

        verify(servicioVideojuego).findById(99L);
    }

    @Test
    void testCreateTemplateMethod() throws Exception {
        Videojuego saved = new Videojuego();
        saved.setId(10L);
        saved.setTitulo("Metroid Prime");

        when(servicioVideojuego.save(any(Videojuego.class))).thenReturn(saved);

        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Metroid Prime\",\"precio\":50.0,\"stock\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Metroid Prime"));

        verify(servicioVideojuego).save(any(Videojuego.class));
    }

    @Test
    void testDeleteTemplateMethod() throws Exception {
        mockMvc.perform(delete("/api/videojuegos/1"))
                .andExpect(status().isNoContent());

        verify(servicioVideojuego).deleteById(1L);
    }
}
