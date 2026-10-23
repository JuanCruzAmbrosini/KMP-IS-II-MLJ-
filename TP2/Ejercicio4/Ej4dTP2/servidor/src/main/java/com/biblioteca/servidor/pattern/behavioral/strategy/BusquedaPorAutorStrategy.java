package com.biblioteca.servidor.pattern.behavioral.strategy;

import com.biblioteca.servidor.model.Libro;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Patrón Comportamiento Strategy:
 * Estrategia concreta que busca libros examinando el campo textual de autor
 * y la lista de entidades Autor vinculadas al libro.
 */
@Component
public class BusquedaPorAutorStrategy implements BusquedaLibroStrategy {

    @Override
    public String getNombreEstrategia() {
        return "AUTOR";
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
                .filter(libro -> {
                    // 1. Verificación por autor textual de cabecera
                    boolean coincideTexto = libro.getAutor() != null &&
                            libro.getAutor().toLowerCase().contains(term);
                    if (coincideTexto) {
                        return true;
                    }

                    // 2. Verificación en colección de entidades Autor asociadas
                    if (libro.getAutores() != null) {
                        return libro.getAutores().stream().anyMatch(a -> {
                            String nombreCompleto = ((a.getNombre() != null ? a.getNombre() : "") + " " +
                                    (a.getApellido() != null ? a.getApellido() : "")).toLowerCase();
                            return nombreCompleto.contains(term);
                        });
                    }
                    return false;
                })
                .toList();
    }
}

