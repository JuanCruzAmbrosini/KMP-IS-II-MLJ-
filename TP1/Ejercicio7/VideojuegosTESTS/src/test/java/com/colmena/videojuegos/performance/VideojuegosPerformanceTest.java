package com.colmena.videojuegos.performance;

import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * TIPO DE PRUEBA: PRUEBAS DE RENDIMIENTO (PERFORMANCE TESTING)
 * Objetivo: Medición de velocidad, tiempos de respuesta y estabilidad de operaciones clave,
 * asegurando que cumplan con los Acuerdos de Nivel de Servicio (SLA < 150 ms).
 */
@SpringBootTest
public class VideojuegosPerformanceTest {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Test
    @DisplayName("Performance Test: La consulta de catalogo completo debe responder en menos de 150 milisegundos")
    void testPerformance_FindAllPorDebajoDeSLA() {
        assertTimeoutPreemptively(Duration.ofMillis(150), () -> {
            long inicio = System.currentTimeMillis();
            var resultados = servicioVideojuego.findAllByActivo();
            long fin = System.currentTimeMillis();
            assertNotNull(resultados);
            System.out.println("[PERFORMANCE] Tiempo de respuesta findAllByActivo: " + (fin - inicio) + " ms");
        });
    }

    @Test
    @DisplayName("Performance Test: 100 consultas consecutivas de busqueda deben completarse en menos de 500 milisegundos")
    void testPerformance_ConsultasConsecutivas() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            long inicio = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                servicioVideojuego.findByTitle("Zelda");
            }
            long fin = System.currentTimeMillis();
            System.out.println("[PERFORMANCE] Tiempo para 100 busquedas consecutivas: " + (fin - inicio) + " ms");
        });
    }
}

