package com.biblioteca.servidor.pattern.behavioral.strategy;

import com.biblioteca.servidor.model.Libro;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Patrón Comportamiento Strategy:
 * Estrategia concreta que busca libros comparando el género literario.
 */
@Component
public class BusquedaPorGeneroStrategy implements BusquedaLibroStrategy {

    @Override
    public String getNombreEstrategia() {
        return "GENERO";
    }

    @Override
    public List<Libro> buscar(List<Libro> libros, String criterio) {
        if (libros == null || libros.isEmpty()) {
            return Collections.emptyList();
        }
        if (criterio == null || criterio.isBlank()) {
            return libros;
        }

        final String term = criterio.trim().toLowerCase();
        return libros.stream()
                .filter(l -> l.getGenero() != null && l.getGenero().toLowerCase().contains(term))
                .toList();
    }
}

