package com.colmena.videojuegos.performance;

import com.colmena.videojuegos.services.ServicioVideojuego;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TIPO DE PRUEBA: PRUEBAS DE CARGA (LOAD TESTING) Y PRUEBAS DE ESTRÉS (STRESS TESTING)
 * Objetivo:
 * - Carga: Evaluar el comportamiento ante la concurrencia normal/pico de usuarios esperados.
 * - Estrés: Someter el sistema a una ráfaga masiva y simultánea de peticiones concurrentes
 *   para verificar el punto de ruptura, estabilidad de memoria y ausencia de bloqueos o deadlocks.
 */
@SpringBootTest
public class VideojuegosLoadAndStressTest {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Test
    @DisplayName("Load Testing: Simulacion de 30 usuarios concurrentes consultando catalogo")
    void testPruebaDeCarga_UsuariosConcurrentes() throws InterruptedException {
        int cantidadUsuarios = 30;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(cantidadUsuarios);
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger fallos = new AtomicInteger(0);

        long tiempoInicio = System.currentTimeMillis();

        for (int i = 0; i < cantidadUsuarios; i++) {
            executor.submit(() -> {
                try {
                    startSignal.await(); // Esperar para iniciar todos en simultáneo
                    servicioVideojuego.findAllByActivo();
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    fallos.incrementAndGet();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown(); // Disparar concurrencia
        boolean completado = doneSignal.await(5, TimeUnit.SECONDS);
        long duracionTotal = System.currentTimeMillis() - tiempoInicio;
        executor.shutdown();

        System.out.println("=== [LOAD TESTING RESULTS] ===");
        System.out.println("Usuarios concurrentes simulados: " + cantidadUsuarios);
        System.out.println("Peticiones exitosas: " + exitos.get());
        System.out.println("Peticiones fallidas: " + fallos.get());
        System.out.println("Duracion total: " + duracionTotal + " ms");

        assertTrue(completado, "La prueba de carga debio completarse en el tiempo limite");
        assertEquals(cantidadUsuarios, exitos.get(), "Todas las peticiones bajo carga normal deben completarse exitosamente");
        assertEquals(0, fallos.get(), "No deben ocurrir fallos en pruebas de carga");
    }

    @Test
    @DisplayName("Stress Testing: Rafaga extrema de 100 peticiones simultaneas para evaluar saturacion y recuperacion")
    void testPruebaDeEstres_RafagaExtrema() throws InterruptedException {
        int peticionesExtremas = 100;
        ExecutorService executor = Executors.newFixedThreadPool(25);
        CountDownLatch disparoSimultaneo = new CountDownLatch(1);
        CountDownLatch finalizacion = new CountDownLatch(peticionesExtremas);
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        long inicio = System.currentTimeMillis();

        for (int i = 0; i < peticionesExtremas; i++) {
            final int peticionId = i;
            executor.submit(() -> {
                try {
                    disparoSimultaneo.await();
                    // Intercalamos operaciones de busqueda y consulta
                    if (peticionId % 2 == 0) {
                        servicioVideojuego.findAllByActivo();
                    } else {
                        servicioVideojuego.findByTitle("Zelda");
                    }
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    errores.incrementAndGet();
                } finally {
                    finalizacion.countDown();
                }
            });
        }

        disparoSimultaneo.countDown(); // Disparo de golpe (estrés máximo)
        boolean finalizado = finalizacion.await(10, TimeUnit.SECONDS);
        long duracion = System.currentTimeMillis() - inicio;
        executor.shutdown();

        System.out.println("=== [STRESS TESTING RESULTS] ===");
        System.out.println("Peticiones en rafaga extrema: " + peticionesExtremas);
        System.out.println("Exitos procesados: " + exitos.get());
        System.out.println("Errores bajo estres: " + errores.get());
        System.out.println("Tiempo de procesamiento: " + duracion + " ms");

        assertTrue(finalizado, "El sistema debe recuperarse y finalizar todas las tareas dentro de la ventana de estres");
        assertTrue(exitos.get() >= (peticionesExtremas * 0.95), "Al menos el 95% de las transacciones extremas deben completarse de forma robusta");
    }
}

