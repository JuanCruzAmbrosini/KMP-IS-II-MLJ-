package com.colmena.videojuegos.integration;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBA DE INTEGRACIÓN (INTEGRATION TESTING)
 * Objetivo: Evaluar la interacción y comunicación real entre la capa de servicios,
 * los repositorios Spring Data JPA y la base de datos (H2 in-memory),
 * verificando persistencia, transacciones y relaciones @ManyToOne.
 */
@SpringBootTest
@Transactional
public class VideojuegoIntegrationTest {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Autowired
    private RepositorioCategoria repositorioCategoria;

    @Autowired
    private RepositorioEstudio repositorioEstudio;

    @Test
    @DisplayName("Integration Test: Guardar un videojuego con relaciones y recuperarlo por titulo")
    void testGuardarYBuscarVideojuegoIntegrado() throws Exception {
        // 1. Guardar categoria y estudio en base de datos real en memoria
        Categoria categoria = new Categoria();
        categoria.setNombre("Accion");
        categoria.setActivo(true);
        categoria = repositorioCategoria.save(categoria);

        Estudio estudio = new Estudio();
        estudio.setNombre("Capcom");
        estudio.setActivo(true);
        estudio = repositorioEstudio.save(estudio);

        // 2. Persistir videojuego a través del servicio
        Videojuego vj = new Videojuego();
        vj.setTitulo("Resident Evil 4 Remake");
        vj.setDescripcion("Survival horror clasico remasterizado");
        vj.setPrecio(69.99f);
        vj.setStock((short) 15);
        vj.setFechaLanzamiento(new Date());
        vj.setActivo(true);
        vj.setCategoria(categoria);
        vj.setEstudio(estudio);

        Videojuego guardado = servicioVideojuego.saveOne(vj);

        assertNotNull(guardado.getId(), "El ID generado por la base de datos no debe ser nulo");
        assertTrue(guardado.getId() > 0);

        // 3. Buscar por titulo mediante query derivada de Spring Data JPA
        List<Videojuego> encontrados = servicioVideojuego.findByTitle("Resident");
        assertFalse(encontrados.isEmpty(), "Debe encontrar al menos un videojuego coincidente");
        assertEquals("Resident Evil 4 Remake", encontrados.get(0).getTitulo());
        assertEquals("Capcom", encontrados.get(0).getEstudio().getNombre());
        assertEquals("Accion", encontrados.get(0).getCategoria().getNombre());

        // 4. Verificar listado de activos
        List<Videojuego> activos = servicioVideojuego.findAllByActivo();
        assertTrue(activos.stream().anyMatch(v -> v.getTitulo().equals("Resident Evil 4 Remake")));
    }
}

