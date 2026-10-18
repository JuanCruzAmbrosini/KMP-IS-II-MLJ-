package com.colmena.videojuegos;

import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTemplateMethodTest {

    @Mock
    private RepositorioVideojuego repositorioVideojuego;

    @InjectMocks
    private ServicioVideojuego servicioVideojuego;

    @Test
    void testFindAllTemplateMethod() {
        Videojuego v = new Videojuego();
        v.setId(1L);
        v.setTitulo("Dark Souls");

        when(repositorioVideojuego.findAll()).thenReturn(Arrays.asList(v));

        List<Videojuego> result = servicioVideojuego.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dark Souls", result.get(0).getTitulo());
        verify(repositorioVideojuego).findAll();
    }

    @Test
    void testSaveTemplateMethod() {
        Videojuego toSave = new Videojuego();
        toSave.setTitulo("Elden Ring");

        Videojuego saved = new Videojuego();
        saved.setId(1L);
        saved.setTitulo("Elden Ring");

        when(repositorioVideojuego.save(any(Videojuego.class))).thenReturn(saved);

        Videojuego result = servicioVideojuego.save(toSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Elden Ring", result.getTitulo());
        verify(repositorioVideojuego).save(toSave);
    }

    @Test
    void testUpdateTemplateMethod() {
        Videojuego existente = new Videojuego();
        existente.setId(1L);
        existente.setTitulo("Hollow Knight");

        Videojuego cambios = new Videojuego();
        cambios.setTitulo("Hollow Knight: Silksong");
        cambios.setPrecio(30.0f);

        when(repositorioVideojuego.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorioVideojuego.save(existente)).thenReturn(existente);

        Videojuego result = servicioVideojuego.update(1L, cambios);

        assertNotNull(result);
        assertEquals("Hollow Knight: Silksong", result.getTitulo());
        verify(repositorioVideojuego).findById(1L);
        verify(repositorioVideojuego).save(existente);
    }

    @Test
    void testDeleteByIdLogicalDeletionTemplateMethod() {
        Videojuego existente = new Videojuego();
        existente.setId(1L);
        existente.setActivo(true);

        when(repositorioVideojuego.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorioVideojuego.save(existente)).thenReturn(existente);

        servicioVideojuego.deleteById(1L);

        assertFalse(existente.isActivo());
        verify(repositorioVideojuego).save(existente);
    }
}
