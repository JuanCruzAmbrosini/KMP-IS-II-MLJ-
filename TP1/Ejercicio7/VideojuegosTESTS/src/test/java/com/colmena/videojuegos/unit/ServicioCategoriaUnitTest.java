package com.colmena.videojuegos.unit;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import com.colmena.videojuegos.services.ServicioCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TIPO DE PRUEBA: PRUEBA UNITARIA (UNIT TESTING)
 * Objetivo: Validar la lógica aislada del servicio de Categorías utilizando dobles de prueba.
 */
@ExtendWith(MockitoExtension.class)
public class ServicioCategoriaUnitTest {

    @Mock
    private RepositorioCategoria repositorioCategoria;

    @InjectMocks
    private ServicioCategoria servicioCategoria;

    private Categoria categoriaMock;

    @BeforeEach
    void setUp() {
        categoriaMock = new Categoria();
        categoriaMock.setId(1L);
        categoriaMock.setNombre("RPG");
        categoriaMock.setActivo(true);
    }

    @Test
    @DisplayName("Unit Test: findAll de Categoria retorna la lista esperada")
    void testFindAll_RetornaLista() throws Exception {
        when(repositorioCategoria.findAll()).thenReturn(Arrays.asList(categoriaMock));

        List<Categoria> resultado = servicioCategoria.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("RPG", resultado.get(0).getNombre());
        verify(repositorioCategoria, times(1)).findAll();
    }

    @Test
    @DisplayName("Unit Test: findById de Categoria con ID existente")
    void testFindById_Existente() throws Exception {
        when(repositorioCategoria.findById(1L)).thenReturn(Optional.of(categoriaMock));

        Categoria resultado = servicioCategoria.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("RPG", resultado.getNombre());
        verify(repositorioCategoria, times(1)).findById(1L);
    }
}

