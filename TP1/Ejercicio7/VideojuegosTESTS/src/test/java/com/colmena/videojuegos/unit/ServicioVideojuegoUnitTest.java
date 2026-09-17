package com.colmena.videojuegos.unit;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TIPO DE PRUEBA: PRUEBA UNITARIA (UNIT TESTING)
 * Objetivo: Verificar el funcionamiento aislado de los métodos del servicio de videojuegos,
 * aislando el acceso a base de datos mediante dobles de prueba (Mocks con Mockito).
 */
@ExtendWith(MockitoExtension.class)
public class ServicioVideojuegoUnitTest {

    @Mock
    private RepositorioVideojuego repositorioVideojuego;

    @InjectMocks
    private ServicioVideojuego servicioVideojuego;

    private Videojuego videojuegoMock;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Aventura");

        Estudio estudio = new Estudio();
        estudio.setId(1L);
        estudio.setNombre("Nintendo");

        videojuegoMock = new Videojuego();
        videojuegoMock.setId(1L);
        videojuegoMock.setTitulo("The Legend of Zelda");
        videojuegoMock.setDescripcion("Aventura epica en Hyrule");
        videojuegoMock.setPrecio(59.99f);
        videojuegoMock.setStock((short) 25);
        videojuegoMock.setFechaLanzamiento(new Date());
        videojuegoMock.setActivo(true);
        videojuegoMock.setCategoria(categoria);
        videojuegoMock.setEstudio(estudio);
    }

    @Test
    @DisplayName("Unit Test: findAll debe retornar la lista de videojuegos")
    void testFindAll_DebeRetornarLista() throws Exception {
        when(repositorioVideojuego.findAll()).thenReturn(Arrays.asList(videojuegoMock));

        List<Videojuego> resultado = servicioVideojuego.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("The Legend of Zelda", resultado.get(0).getTitulo());
        verify(repositorioVideojuego, times(1)).findAll();
    }

    @Test
    @DisplayName("Unit Test: findById con ID existente retorna el videojuego")
    void testFindById_Existente_RetornaVideojuego() throws Exception {
        when(repositorioVideojuego.findById(1L)).thenReturn(Optional.of(videojuegoMock));

        Videojuego resultado = servicioVideojuego.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("The Legend of Zelda", resultado.getTitulo());
        verify(repositorioVideojuego, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Unit Test: findById con ID inexistente lanza excepcion")
    void testFindById_Inexistente_LanzaExcepcion() {
        when(repositorioVideojuego.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            servicioVideojuego.findById(99L);
        });
        verify(repositorioVideojuego, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Unit Test: saveOne guarda y retorna la entidad")
    void testSaveOne_GuardaYRetornaEntidad() throws Exception {
        when(repositorioVideojuego.save(any(Videojuego.class))).thenReturn(videojuegoMock);

        Videojuego guardado = servicioVideojuego.saveOne(videojuegoMock);

        assertNotNull(guardado);
        assertEquals("The Legend of Zelda", guardado.getTitulo());
        verify(repositorioVideojuego, times(1)).save(videojuegoMock);
    }

    @Test
    @DisplayName("Unit Test: deleteById alterna el estado 'activo' a falso (baja logica)")
    void testDeleteById_AlternaEstadoActivo() throws Exception {
        when(repositorioVideojuego.findById(1L)).thenReturn(Optional.of(videojuegoMock));
        when(repositorioVideojuego.save(any(Videojuego.class))).thenReturn(videojuegoMock);

        boolean resultado = servicioVideojuego.deleteById(1L);

        assertTrue(resultado);
        assertFalse(videojuegoMock.isActivo(), "El estado activo debio cambiar a falso");
        verify(repositorioVideojuego, times(1)).findById(1L);
        verify(repositorioVideojuego, times(1)).save(videojuegoMock);
    }
}

