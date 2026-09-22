package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.AuditoriaVideojuego;
import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioAuditoriaVideojuego;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioVideojuegoTest {

    @InjectMocks
    private ServicioVideojuego servicioVideojuego;

    @Mock
    private RepositorioVideojuego repositorio;

    @Mock
    private RepositorioAuditoriaVideojuego repositorioAuditoria;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() throws Exception {
        List<Videojuego> videojuegos = new ArrayList<>();
        videojuegos.add(new Videojuego());
        when(repositorio.findAll()).thenReturn(videojuegos);

        List<Videojuego> result = servicioVideojuego.findAll();

        assertEquals(1, result.size());
        verify(repositorio, times(1)).findAll();
    }

    @Test
    public void testFindById() throws Exception {
        Videojuego videojuego = new Videojuego();
        videojuego.setId(1L);
        when(repositorio.findById(1L)).thenReturn(Optional.of(videojuego));

        Videojuego result = servicioVideojuego.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repositorio, times(1)).findById(1L);
    }

    @Test
    public void testSaveOne() throws Exception {
        Videojuego videojuego = new Videojuego();
        videojuego.setTitulo("Test");
        
        when(repositorio.save(any(Videojuego.class))).thenReturn(videojuego);

        Videojuego result = servicioVideojuego.saveOne(videojuego);

        assertNotNull(result);
        verify(repositorio, times(1)).save(any(Videojuego.class));
        verify(repositorioAuditoria, atLeastOnce()).save(any(AuditoriaVideojuego.class));
    }

    @Test
    public void testUpdateOne() throws Exception {
        Videojuego anterior = new Videojuego();
        anterior.setId(1L);
        anterior.setTitulo("Viejo");

        Videojuego nuevo = new Videojuego();
        nuevo.setId(1L);
        nuevo.setTitulo("Nuevo");

        when(repositorio.findById(1L)).thenReturn(Optional.of(anterior));
        when(repositorio.save(any(Videojuego.class))).thenReturn(nuevo);

        Videojuego result = servicioVideojuego.updateOne(nuevo, 1L);

        assertNotNull(result);
        assertEquals("Nuevo", result.getTitulo());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(nuevo);
        verify(repositorioAuditoria, atLeastOnce()).save(any(AuditoriaVideojuego.class));
    }

    @Test
    public void testDeleteById() throws Exception {
        Videojuego videojuego = new Videojuego();
        videojuego.setId(1L);
        videojuego.setActivo(true);

        when(repositorio.findById(1L)).thenReturn(Optional.of(videojuego));
        when(repositorio.save(any(Videojuego.class))).thenReturn(videojuego);

        boolean result = servicioVideojuego.deleteById(1L);

        assertTrue(result);
        assertFalse(videojuego.isActivo());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(videojuego);
        verify(repositorioAuditoria, atLeastOnce()).save(any(AuditoriaVideojuego.class));
    }

    @Test
    public void testFindAllByActivo() throws Exception {
        List<Videojuego> videojuegos = new ArrayList<>();
        videojuegos.add(new Videojuego());
        when(repositorio.findAllByActivo()).thenReturn(videojuegos);

        List<Videojuego> result = servicioVideojuego.findAllByActivo();

        assertEquals(1, result.size());
        verify(repositorio, times(1)).findAllByActivo();
    }
}
