package com.colmena.videojuegos.acceptance;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBAS DE ACEPTACIÓN (UAT / BDD)
 * Objetivo: Confirmar que el sistema satisface los criterios de aceptación y necesidades
 * del negocio redactadas desde la perspectiva del usuario final (estructura Given-When-Then).
 *
 * HISTORIA DE USUARIO:
 * "Como aficionado a los videojuegos, quiero consultar los títulos activos en el catálogo
 * y poder buscar juegos por palabra clave para decidir qué videojuego adquirir."
 */
@SpringBootTest
@Transactional
public class CatalogoVideojuegosAcceptanceTest {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Autowired
    private RepositorioCategoria repositorioCategoria;

    @Autowired
    private RepositorioEstudio repositorioEstudio;

    private Videojuego juego1;
    private Videojuego juego2;

    @BeforeEach
    void setupContext() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Plataformas");
        categoria.setActivo(true);
        categoria = repositorioCategoria.save(categoria);

        Estudio estudio = new Estudio();
        estudio.setNombre("Nintendo EPD");
        estudio.setActivo(true);
        estudio = repositorioEstudio.save(estudio);

        juego1 = new Videojuego();
        juego1.setTitulo("Super Mario Odyssey");
        juego1.setDescripcion("Aventura 3D de Mario a traves de multiples reinos");
        juego1.setPrecio(59.99f);
        juego1.setStock((short) 50);
        juego1.setFechaLanzamiento(new Date());
        juego1.setActivo(true);
        juego1.setCategoria(categoria);
        juego1.setEstudio(estudio);
        servicioVideojuego.saveOne(juego1);

        juego2 = new Videojuego();
        juego2.setTitulo("Super Mario Bros Wonder");
        juego2.setDescripcion("Nueva entrega 2D con efectos maravillosos");
        juego2.setPrecio(49.99f);
        juego2.setStock((short) 40);
        juego2.setFechaLanzamiento(new Date());
        juego2.setActivo(true);
        juego2.setCategoria(categoria);
        juego2.setEstudio(estudio);
        servicioVideojuego.saveOne(juego2);
    }

    @Test
    @DisplayName("Criterio de Aceptacion 1: Visualizacion de videojuegos activos en el catalogo")
    void escenario_VisualizacionCatalogoActivos() throws Exception {
        // GIVEN (Dado): Existen videojuegos publicados y activos en el sistema
        assertNotNull(juego1.getId());
        assertNotNull(juego2.getId());

        // WHEN (Cuando): El cliente accede al catalogo de videojuegos activos
        List<Videojuego> catalogo = servicioVideojuego.findAllByActivo();

        // THEN (Entonces): Se visualizan unicamente los juegos con estado activo = true
        assertFalse(catalogo.isEmpty(), "El catalogo debe mostrar los productos");
        assertTrue(catalogo.stream().allMatch(Videojuego::isActivo), "Todos los juegos listados deben estar activos");
        assertTrue(catalogo.stream().anyMatch(v -> v.getTitulo().contains("Mario")));
    }

    @Test
    @DisplayName("Criterio de Aceptacion 2: Busqueda precisa por coincidencia en el titulo")
    void escenario_BusquedaPorPalabraClave() throws Exception {
        // GIVEN (Dado): Existen titulos con la palabra 'Odyssey' y 'Wonder'
        String terminoBusqueda = "Odyssey";

        // WHEN (Cuando): El usuario realiza una busqueda filtrando por dicho termino
        List<Videojuego> resultados = servicioVideojuego.findByTitle(terminoBusqueda);

        // THEN (Entonces): Se retorna unicamente el juego coincidente y sus atributos de negocio
        assertEquals(1, resultados.size(), "Debe haber exactamente un resultado para 'Odyssey'");
        Videojuego resultado = resultados.get(0);
        assertEquals("Super Mario Odyssey", resultado.getTitulo());
        assertEquals(59.99f, resultado.getPrecio());
        assertTrue(resultado.getStock() > 0, "El juego debe tener stock disponible para la venta");
    }
}

