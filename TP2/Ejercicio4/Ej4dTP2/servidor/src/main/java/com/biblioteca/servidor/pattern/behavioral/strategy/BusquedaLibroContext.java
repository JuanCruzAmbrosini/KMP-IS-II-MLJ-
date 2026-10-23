package com.biblioteca.servidor.pattern.behavioral.strategy;

import com.biblioteca.servidor.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Patrón Comportamiento Strategy:
 * Contexto que mantiene la referencia a la estrategia de búsqueda activa y coordina su ejecución.
 * Permite cambiar dinámicamente el algoritmo utilizado para consultar los libros.
 */
@Component
public class BusquedaLibroContext {

    private final Map<String, BusquedaLibroStrategy> strategies;
    private BusquedaLibroStrategy currentStrategy;

    @Autowired
    public BusquedaLibroContext(List<BusquedaLibroStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s -> s.getNombreEstrategia().toUpperCase(), s -> s));
        // Estrategia por defecto: Búsqueda por Título
        this.currentStrategy = strategies.getOrDefault("TITULO", null);
    }

    public BusquedaLibroContext() {
        this.strategies = new java.util.HashMap<>();
    }

    public void setStrategy(BusquedaLibroStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Configura la estrategia a partir de su nombre ("TITULO", "GENERO", "AUTOR").
     */
    public void setStrategyPorNombre(String nombreEstrategia) {
        if (nombreEstrategia == null || nombreEstrategia.isBlank()) {
            this.currentStrategy = strategies.getOrDefault("TITULO", currentStrategy);
            return;
        }
        BusquedaLibroStrategy found = strategies.get(nombreEstrategia.trim().toUpperCase());
        if (found != null) {
            this.currentStrategy = found;
        }
    }

    public BusquedaLibroStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Ejecuta la búsqueda utilizando la estrategia activa.
     */
    public List<Libro> ejecutarBusqueda(List<Libro> libros, String criterio) {
        if (currentStrategy == null) {
            throw new IllegalStateException("No se ha configurado ninguna estrategia de búsqueda.");
        }
        return currentStrategy.buscar(libros, criterio);
    }

    /**
     * Ejecuta la búsqueda seleccionando directamente la estrategia y ejecutándola en un solo paso.
     */
    public List<Libro> ejecutarCon(String tipoEstrategia, List<Libro> libros, String criterio) {
        setStrategyPorNombre(tipoEstrategia);
        return ejecutarBusqueda(libros, criterio);
    }
}

