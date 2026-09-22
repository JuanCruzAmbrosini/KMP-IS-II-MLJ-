package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
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

public class ServicioCategoriaTest {

    @InjectMocks
    private ServicioCategoria servicioCategoria;

    @Mock
    private RepositorioCategoria repositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria());
        when(repositorio.findAll()).thenReturn(categorias);

        List<Categoria> result = servicioCategoria.findAll();

        assertEquals(1, result.size());
        verify(repositorio, times(1)).findAll();
    }

    @Test
    public void testFindById() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        when(repositorio.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria result = servicioCategoria.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repositorio, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdException() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            servicioCategoria.findById(1L);
        });

        assertNotNull(exception);
    }

    @Test
    public void testSaveOne() throws Exception {
        Categoria categoria = new Categoria();
        when(repositorio.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = servicioCategoria.saveOne(new Categoria());

        assertNotNull(result);
        verify(repositorio, times(1)).save(any(Categoria.class));
    }

    @Test
    public void testUpdateOne() throws Exception {
        Categoria categoriaAnterior = new Categoria();
        categoriaAnterior.setId(1L);

        Categoria categoriaNueva = new Categoria();
        categoriaNueva.setId(1L);
        categoriaNueva.setNombre("Nuevo");

        when(repositorio.findById(1L)).thenReturn(Optional.of(categoriaAnterior));
        when(repositorio.save(any(Categoria.class))).thenReturn(categoriaNueva);

        Categoria result = servicioCategoria.updateOne(categoriaNueva, 1L);

        assertNotNull(result);
        assertEquals("Nuevo", result.getNombre());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(categoriaNueva);
    }

    @Test
    public void testDeleteById() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setActivo(true);

        when(repositorio.findById(1L)).thenReturn(Optional.of(categoria));
        when(repositorio.save(any(Categoria.class))).thenReturn(categoria);

        boolean result = servicioCategoria.deleteById(1L);

        assertTrue(result);
        assertFalse(categoria.isActivo());
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(categoria);
    }

    @Test
    public void testDeleteByIdException() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            servicioCategoria.deleteById(1L);
        });

        assertNotNull(exception);
    }
}
