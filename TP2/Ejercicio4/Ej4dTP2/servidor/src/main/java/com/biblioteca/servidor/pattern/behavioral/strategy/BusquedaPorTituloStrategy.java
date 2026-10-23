package com.biblioteca.servidor.pattern.behavioral.strategy;

import com.biblioteca.servidor.model.Libro;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Patrón Comportamiento Strategy:
 * Estrategia concreta que busca libros comparando el título (case-insensitive y contención parcial).
 */
@Component
public class BusquedaPorTituloStrategy implements BusquedaLibroStrategy {

    @Override
    public String getNombreEstrategia() {
        return "TITULO";
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
                .filter(l -> l.getTitulo() != null && l.getTitulo().toLowerCase().contains(term))
                .toList();
    }
}

