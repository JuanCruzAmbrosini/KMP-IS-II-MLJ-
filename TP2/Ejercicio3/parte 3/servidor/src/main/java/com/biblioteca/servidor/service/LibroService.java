package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.repository.AutorRepository;
import com.biblioteca.servidor.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibroService extends BaseService<Libro, Long> {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Override
    protected JpaRepository<Libro, Long> getRepository() {
        return libroRepository;
    }

    @Override
    protected String getEntityName() {
        return "Libro";
    }

    @Override
    protected void preSave(Libro libro) {
        if (libro.getAutores() != null) {
            List<Autor> managedAutores = libro.getAutores().stream()
                    .map(a -> autorRepository.findById(a.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            libro.setAutores(new ArrayList<>(managedAutores));
        }
    }

    @Override
    protected void actualizarDatos(Libro existente, Libro nuevo) {
        existente.setTitulo(nuevo.getTitulo());
        existente.setFecha(nuevo.getFecha());
        existente.setGenero(nuevo.getGenero());
        existente.setPaginas(nuevo.getPaginas());
        existente.setAutor(nuevo.getAutor());
        existente.setFechaVencimientoDevolucion(nuevo.getFechaVencimientoDevolucion());
        if (nuevo.getAutores() != null) {
            List<Autor> managedAutores = nuevo.getAutores().stream()
                    .map(a -> autorRepository.findById(a.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            existente.setAutores(new ArrayList<>(managedAutores));
        } else {
            existente.setAutores(new ArrayList<>());
        }
    }
}
