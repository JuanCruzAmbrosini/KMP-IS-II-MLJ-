package com.biblioteca.service;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    public List<Autor> listarActivos() {
        return autorRepository.findAll().stream().filter(Autor::isAlta).toList();
    }

    public Autor buscarPorId(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado: " + id));
    }

    public Autor guardar(Autor autor) {
        return autorRepository.save(autor);
    }

    // Baja logica: nunca se borra fisicamente para no romper el historial de Prestamos/Libros
    public void darDeBaja(Long id) {
        Autor autor = buscarPorId(id);
        autor.setAlta(false);
        autorRepository.save(autor);
    }
}
