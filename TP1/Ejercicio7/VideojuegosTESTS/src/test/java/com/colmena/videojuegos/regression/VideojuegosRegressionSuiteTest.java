package com.colmena.videojuegos.regression;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.services.ServicioCategoria;
import com.colmena.videojuegos.services.ServicioEstudio;
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
 * TIPO DE PRUEBA: PRUEBAS DE REGRESIÓN (REGRESSION TESTING)
 * Objetivo: Reejecución automatizada y consolidada de los flujos troncales del sistema
 * para certificar que nuevos cambios, correcciones de errores o refactorizaciones
 * no hayan introducido nuevos defectos ni alterado el comportamiento previo ya validado.
 */
@SpringBootTest
@Transactional
public class VideojuegosRegressionSuiteTest {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Autowired
    private ServicioCategoria servicioCategoria;

    @Autowired
    private ServicioEstudio servicioEstudio;

    @Test
    @DisplayName("Regresion - Caso Troncal 1: Ciclo de vida completo del Videojuego (Crear -> Buscar -> Modificar -> Baja)")
    void testRegresion_CicloDeVidaVideojuego() throws Exception {
        // 1. Preparar dependencias maestras
        Categoria cat = new Categoria();
        cat.setNombre("Estrategia");
        cat.setActivo(true);
        cat = servicioCategoria.saveOne(cat);

        Estudio est = new Estudio();
        est.setNombre("Blizzard");
        est.setActivo(true);
        est = servicioEstudio.saveOne(est);

        // 2. Crear Videojuego
        Videojuego vj = new Videojuego();
        vj.setTitulo("StarCraft II");
        vj.setDescripcion("Estrategia en tiempo real en el sector Koprulu");
        vj.setPrecio(29.99f);
        vj.setStock((short) 100);
        vj.setFechaLanzamiento(new Date());
        vj.setActivo(true);
        vj.setCategoria(cat);
        vj.setEstudio(est);

        Videojuego creado = servicioVideojuego.saveOne(vj);
        assertNotNull(creado.getId(), "El ID debe ser asignado en el alta");

        // 3. Buscar y verificar persistencia
        Videojuego recuperado = servicioVideojuego.findById(creado.getId());
        assertEquals("StarCraft II", recuperado.getTitulo());

        // 4. Modificar precio
        recuperado.setPrecio(19.99f);
        Videojuego modificado = servicioVideojuego.updateOne(recuperado, recuperado.getId());
        assertEquals(19.99f, modificado.getPrecio(), "El precio actualizado debe persistir");

        // 5. Baja lógica
        servicioVideojuego.deleteById(modificado.getId());
        Videojuego dadoDeBaja = servicioVideojuego.findById(modificado.getId());
        assertFalse(dadoDeBaja.isActivo(), "El juego debe figurar como inactivo tras la baja lógica");

        // 6. Confirmar que no aparece en el listado de activos
        List<Videojuego> activos = servicioVideojuego.findAllByActivo();
        assertFalse(activos.stream().anyMatch(v -> v.getId() == modificado.getId()),
                "El juego dado de baja no debe figurar en el catalogo de activos");
    }

    @Test
    @DisplayName("Regresion - Caso Troncal 2: Consulta de Categorias y Estudios")
    void testRegresion_ConsultaMaestros() throws Exception {
        List<Categoria> categorias = servicioCategoria.findAll();
        assertNotNull(categorias);

        List<Estudio> estudios = servicioEstudio.findAll();
        assertNotNull(estudios);
    }
}

