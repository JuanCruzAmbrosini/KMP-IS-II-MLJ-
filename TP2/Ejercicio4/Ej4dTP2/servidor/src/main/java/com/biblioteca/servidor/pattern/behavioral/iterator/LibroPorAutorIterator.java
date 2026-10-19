package com.biblioteca.servidor.pattern.behavioral.iterator;

import com.biblioteca.servidor.model.Libro;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Patrón Comportamiento Iterator:
 * Iterador concreto que recorre una colección de libros y filtra dinámicamente
 * únicamente aquellos ejemplares pertenecientes a un autor específico (por nombre/apellido o ID).
 */
public class LibroPorAutorIterator implements LibroIterator {

    private final List<Libro> libros;
    private final String filtroNombreAutor;
    private final Long filtroAutorId;
    private int posicionActual = 0;
    private int proximaPosicionValida = -1;

    public LibroPorAutorIterator(List<Libro> libros, String filtroNombreAutor) {
        this.libros = libros != null ? libros : List.of();
        this.filtroNombreAutor = filtroNombreAutor != null ? filtroNombreAutor.trim().toLowerCase() : null;
        this.filtroAutorId = null;
    }

    public LibroPorAutorIterator(List<Libro> libros, Long filtroAutorId) {
        this.libros = libros != null ? libros : List.of();
        this.filtroNombreAutor = null;
        this.filtroAutorId = filtroAutorId;
    }

    @Override
    public boolean hasNext() {
        if (proximaPosicionValida >= posicionActual) {
            return true;
        }

        while (posicionActual < libros.size()) {
            Libro actual = libros.get(posicionActual);
            if (perteneceAlAutor(actual)) {
                proximaPosicionValida = posicionActual;
                return true;
            }
            posicionActual++;
        }
        return false;
    }

    @Override
    public Libro next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No existen más libros pertenecientes al autor especificado.");
        }
        Libro libro = libros.get(proximaPosicionValida);
        posicionActual = proximaPosicionValida + 1;
        proximaPosicionValida = -1;
        return libro;
    }

    /**
     * Utilidad que recorre todo el iterador y recolecta los libros en una lista.
     */
    public List<Libro> recolectarTodos() {
        List<Libro> resultado = new ArrayList<>();
        while (this.hasNext()) {
            resultado.add(this.next());
        }
        return resultado;
    }

    private boolean perteneceAlAutor(Libro libro) {
        if (libro == null) {
            return false;
        }

        // 1. Filtrado por Autor ID
        if (filtroAutorId != null) {
            if (libro.getAutores() != null) {
                return libro.getAutores().stream()
                        .anyMatch(a -> a.getId() != null && a.getId().equals(filtroAutorId));
            }
            return false;
        }

        // 2. Filtrado por Nombre/Apellido o Autor descriptivo
        if (filtroNombreAutor != null && !filtroNombreAutor.isBlank()) {
            if (libro.getAutor() != null && libro.getAutor().toLowerCase().contains(filtroNombreAutor)) {
                return true;
            }
            if (libro.getAutores() != null) {
                return libro.getAutores().stream().anyMatch(a -> {
                    String nombreCompleto = ((a.getNombre() != null ? a.getNombre() : "") + " " +
                            (a.getApellido() != null ? a.getApellido() : "")).toLowerCase();
                    return nombreCompleto.contains(filtroNombreAutor);
                });
            }
            return false;
        }

        // Si no se especificó filtro, incluye todos
        return true;
    }
}

