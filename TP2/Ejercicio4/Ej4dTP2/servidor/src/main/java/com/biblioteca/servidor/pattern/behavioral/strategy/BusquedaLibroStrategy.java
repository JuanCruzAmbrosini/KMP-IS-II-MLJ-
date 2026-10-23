package com.biblioteca.servidor.pattern.behavioral.strategy;

import com.biblioteca.servidor.model.Libro;

import java.util.List;

/**
 * Patrón Comportamiento Strategy:
 * Interfaz que define el algoritmo intercambiable para la búsqueda y filtrado de libros.
 * Permite cambiar la estrategia de búsqueda en tiempo de ejecución sin alterar el cliente ni el servicio.
 */
public interface BusquedaLibroStrategy {

    /**
     * Identificador de la estrategia ("TITULO", "GENERO", "AUTOR").
     */
    String getNombreEstrategia();

    /**
     * Ejecuta el algoritmo de búsqueda sobre una colección de libros según el criterio provisto.
     *
     * @param libros Lista de libros sobre la cual aplicar el filtro
     * @param criterio Texto o término de búsqueda ingresado por el usuario
     * @return Lista de libros que cumplen con la estrategia
     */
    List<Libro> buscar(List<Libro> libros, String criterio);
}

