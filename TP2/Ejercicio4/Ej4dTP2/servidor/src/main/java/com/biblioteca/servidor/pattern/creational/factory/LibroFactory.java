package com.biblioteca.servidor.pattern.creational.factory;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;

/**
 * Patrón Creacional Factory Method / Fábrica:
 * Interfaz que define el contrato para la fabricación desacoplada de objetos de la jerarquía Libro.
 */
public interface LibroFactory {

    /**
     * Identificador del tipo de libro que esta fábrica produce ("FISICO" o "DIGITAL").
     */
    String getTipoSoportado();

    /**
     * Fabrica y retorna una instancia concreta de Libro a partir de un LibroDTO.
     * Utiliza internamente el Patrón Builder correspondiente.
     *
     * @param dto Datos de transferencia con las propiedades del libro
     * @return Instancia concreta de Libro (LibroFisico o LibroDigital)
     */
    Libro crearLibro(LibroDTO dto);
}

