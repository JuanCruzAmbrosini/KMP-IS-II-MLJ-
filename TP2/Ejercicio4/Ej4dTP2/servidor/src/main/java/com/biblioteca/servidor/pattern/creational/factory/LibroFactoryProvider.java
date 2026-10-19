package com.biblioteca.servidor.pattern.creational.factory;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Patrón Creacional Fábrica (Factory Provider / Dispatcher):
 * Administra el catálogo de fábricas disponibles (físico y digital) y resuelve
 * dinámicamente cuál fábrica utilizar según el tipo solicitado.
 */
@Component
public class LibroFactoryProvider {

    private final Map<String, LibroFactory> factories;

    @Autowired
    public LibroFactoryProvider(List<LibroFactory> factoryList) {
        this.factories = factoryList.stream()
                .collect(Collectors.toMap(f -> f.getTipoSoportado().toUpperCase(), f -> f));
    }

    /**
     * Obtiene la fábrica correspondiente al tipo especificado ("FISICO" o "DIGITAL").
     * Si no coincide o es nulo, devuelve por defecto la fábrica de libros físicos.
     */
    public LibroFactory getFactory(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return factories.getOrDefault("FISICO", null);
        }
        LibroFactory factory = factories.get(tipo.trim().toUpperCase());
        return factory != null ? factory : factories.getOrDefault("FISICO", null);
    }

    /**
     * Crea un libro polimórfico a partir de su DTO despachando a la fábrica adecuada.
     */
    public Libro crear(LibroDTO dto) {
        if (dto == null) {
            return null;
        }
        LibroFactory factory = getFactory(dto.getTipoLibro());
        if (factory == null) {
            throw new IllegalArgumentException("No existe fábrica configurada para el tipo de libro: " + dto.getTipoLibro());
        }
        return factory.crearLibro(dto);
    }
}

