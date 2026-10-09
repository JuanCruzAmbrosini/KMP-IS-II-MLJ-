package ingsoftware.gatinder.performance;

import ingsoftware.gatinder.service.PetService;
import ingsoftware.gatinder.service.VoteService;
import ingsoftware.gatinder.service.ZoneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * TIPO DE PRUEBA: PRUEBAS DE RENDIMIENTO (PERFORMANCE TESTING)
 * Objetivo: Medir tiempos de respuesta y latencia de consultas criticas
 * (como la generacion de reportes consolidados y listado de mascotas) bajo umbrales SLA.
 */
@SpringBootTest
public class GatinderPerformanceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ZoneService zoneService;

    @Test
    @DisplayName("Performance Test: Consulta de todas las mascotas debe responder en menos de 100 ms")
    void testPerformance_ListadoMascotas() {
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            long inicio = System.currentTimeMillis();
            var mascotas = petService.findAll();
            long fin = System.currentTimeMillis();
            assertNotNull(mascotas);
            System.out.println("[PERFORMANCE GATINDER] Tiempo listado mascotas: " + (fin - inicio) + " ms");
        });
    }

    @Test
    @DisplayName("Performance Test: Generacion de reporte de votos debe responder en menos de 150 ms")
    void testPerformance_ReporteVotos() {
        assertTimeoutPreemptively(Duration.ofMillis(150), () -> {
            long inicio = System.currentTimeMillis();
            var reporte = voteService.buildVoteReport();
            long fin = System.currentTimeMillis();
            assertNotNull(reporte);
            System.out.println("[PERFORMANCE GATINDER] Tiempo reporte de votos: " + (fin - inicio) + " ms");
        });
    }
}

