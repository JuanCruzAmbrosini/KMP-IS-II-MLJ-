package com.biblioteca.service;

import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public List<Libro> listarActivos() {
        return libroRepository.findAll().stream().filter(Libro::isAlta).toList();
    }

    public Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + id));
    }

    public Libro guardar(Libro libro) {
        // ejemplaresRestantes se recalcula siempre a partir de ejemplares y prestados
        libro.setEjemplaresRestantes(libro.getEjemplares() - libro.getEjemplaresPrestados());
        return libroRepository.save(libro);
    }

    public void darDeBaja(Long id) {
        Libro libro = buscarPorId(id);
        libro.setAlta(false);
        libroRepository.save(libro);
    }
}
