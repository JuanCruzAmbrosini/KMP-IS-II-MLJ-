package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private com.biblioteca.servidor.repository.AutorRepository autorRepository;

    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    public Optional<Libro> findById(Long id) {
        return libroRepository.findById(id);
    }

    public Libro save(Libro libro) {
        if (libro.getAutores() != null) {
            List<Autor> managedAutores = libro.getAutores().stream()
                    .map(a -> autorRepository.findById(a.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            libro.setAutores(new ArrayList<>(managedAutores));
        }
        return libroRepository.save(libro);
    }

    public Libro update(Long id, Libro updated) {
        return libroRepository.findById(id)
                .map(lib -> {
                    lib.setTitulo(updated.getTitulo());
                    lib.setFecha(updated.getFecha());
                    lib.setGenero(updated.getGenero());
                    lib.setPaginas(updated.getPaginas());
                    lib.setAutor(updated.getAutor());
                    lib.setFechaVencimientoDevolucion(updated.getFechaVencimientoDevolucion());
                    if (updated.getAutores() != null) {
                        List<Autor> managedAutores = updated.getAutores().stream()
                                .map(a -> autorRepository.findById(a.getId()).orElse(null))
                                .filter(java.util.Objects::nonNull)
                                .toList();
                        lib.setAutores(new ArrayList<>(managedAutores));
                    } else {
                        lib.setAutores(new ArrayList<>());
                    }
                    return libroRepository.save(lib);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        libroRepository.deleteById(id);
    }
}
