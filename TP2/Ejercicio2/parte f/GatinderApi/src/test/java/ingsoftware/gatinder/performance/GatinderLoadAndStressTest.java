package ingsoftware.gatinder.performance;

import ingsoftware.gatinder.service.PetService;
import ingsoftware.gatinder.service.VoteService;
import ingsoftware.gatinder.service.ZoneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TIPO DE PRUEBA: PRUEBAS DE CARGA (LOAD TESTING) Y PRUEBAS DE ESTRÉS (STRESS TESTING)
 * Objetivo:
 * - Carga: Evaluar el comportamiento del sistema simulando la demanda concurrente esperada.
 * - Estrés: Someter el sistema a ráfagas concurrentes extremas para verificar el punto de ruptura,
 *   integridad de los datos y recuperación elegante ante sobrecarga transaccional.
 */
@SpringBootTest
public class GatinderLoadAndStressTest {

    @Autowired
    private PetService petService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ZoneService zoneService;

    @Test
    @DisplayName("Load Testing: 30 usuarios concurrentes consultando zonas y mascotas")
    void testPruebaDeCarga_UsuariosConcurrentes() throws InterruptedException {
        int totalUsuarios = 30;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(totalUsuarios);
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger fallos = new AtomicInteger(0);

        long inicio = System.currentTimeMillis();

        for (int i = 0; i < totalUsuarios; i++) {
            executor.submit(() -> {
                try {
                    startSignal.await();
                    petService.findAll();
                    zoneService.findAll();
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    fallos.incrementAndGet();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();
        boolean terminado = doneSignal.await(5, TimeUnit.SECONDS);
        long duracion = System.currentTimeMillis() - inicio;
        executor.shutdown();

        System.out.println("=== [GATINDER LOAD TEST RESULTS] ===");
        System.out.println("Usuarios simulados: " + totalUsuarios);
        System.out.println("Exitos: " + exitos.get());
        System.out.println("Fallos: " + fallos.get());
        System.out.println("Duracion: " + duracion + " ms");

        assertTrue(terminado, "La prueba de carga debio culminar en el tiempo estipulado");
        assertEquals(totalUsuarios, exitos.get());
        assertEquals(0, fallos.get());
    }

    @Test
    @DisplayName("Stress Testing: Rafaga extrema de 100 peticiones simultaneas sobre el modulo de reportes")
    void testPruebaDeEstres_RafagaReportes() throws InterruptedException {
        int peticionesExtremas = 100;
        ExecutorService executor = Executors.newFixedThreadPool(25);
        CountDownLatch gatilloSimultaneo = new CountDownLatch(1);
        CountDownLatch barreraFin = new CountDownLatch(peticionesExtremas);
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        long inicio = System.currentTimeMillis();

        for (int i = 0; i < peticionesExtremas; i++) {
            executor.submit(() -> {
                try {
                    gatilloSimultaneo.await();
                    voteService.buildVoteReport();
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    errores.incrementAndGet();
                } finally {
                    barreraFin.countDown();
                }
            });
        }

        gatilloSimultaneo.countDown(); // Disparo de sobrecarga simultanea
        boolean finalizado = barreraFin.await(10, TimeUnit.SECONDS);
        long duracion = System.currentTimeMillis() - inicio;
        executor.shutdown();

        System.out.println("=== [GATINDER STRESS TEST RESULTS] ===");
        System.out.println("Peticiones extremas: " + peticionesExtremas);
        System.out.println("Exitos: " + exitos.get());
        System.out.println("Errores: " + errores.get());
        System.out.println("Tiempo total: " + duracion + " ms");

        assertTrue(finalizado, "El sistema debio procesar la sobrecarga sin colapsar");
        assertTrue(exitos.get() >= (peticionesExtremas * 0.95), "El 95% o mas de las peticiones deben procesarse con exito");
    }
}

