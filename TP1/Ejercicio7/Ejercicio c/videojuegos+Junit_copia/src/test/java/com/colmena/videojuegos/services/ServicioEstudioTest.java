package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
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

public class ServicioEstudioTest {

    @InjectMocks
    private ServicioEstudio servicioEstudio;

    @Mock
    private RepositorioEstudio repositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() throws Exception {
        List<Estudio> estudios = new ArrayList<>();
        estudios.add(new Estudio());
        when(repositorio.findAll()).thenReturn(estudios);

        List<Estudio> result = servicioEstudio.findAll();

        assertEquals(1, result.size());
        verify(repositorio, times(1)).findAll();
    }

    @Test
    public void testFindById() throws Exception {
        Estudio estudio = new Estudio();
        estudio.setId(1L);
        when(repositorio.findById(1L)).thenReturn(Optional.of(estudio));

        Estudio result = servicioEstudio.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repositorio, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdException() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            servicioEstudio.findById(1L);
        });

        assertNotNull(exception);
    }

    @Test
    public void testSaveOne() throws Exception {
        Estudio estudio = new Estudio();
        when(repositorio.save(any(Estudio.class))).thenReturn(estudio);

        Estudio result = servicioEstudio.saveOne(new Estudio());

        assertNotNull(result);
        verify(repositorio, times(1)).save(any(Estudio.class));
    }

    @Test
    public void testUpdateOne() throws Exception {
        Estudio estudioAnterior = new Estudio();
        estudioAnterior.setId(1L);

        Estudio estudioNuevo = new Estudio();
        estudioNuevo.setId(1L);
        estudioNuevo.setNombre("Nuevo");

        when(repositorio.findById(1L)).thenReturn(Optional.of(estudioAnterior));
        when(repositorio.save(any(Estudio.class))).thenReturn(estudioNuevo);

        Estudio result = servicioEstudio.updateOne(estudioNuevo, 1L);

        assertNotNull(result);
        assertEquals("Nuevo", result.getNombre());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(estudioNuevo);
    }

    @Test
    public void testDeleteById() throws Exception {
        Estudio estudio = new Estudio();
        estudio.setId(1L);
        estudio.setActivo(true);

        when(repositorio.findById(1L)).thenReturn(Optional.of(estudio));
        when(repositorio.save(any(Estudio.class))).thenReturn(estudio);

        boolean result = servicioEstudio.deleteById(1L);

        assertTrue(result);
        assertFalse(estudio.isActivo());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(estudio);
    }

    @Test
    public void testDeleteByIdException() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            servicioEstudio.deleteById(1L);
        });

        assertNotNull(exception);
    }
}
