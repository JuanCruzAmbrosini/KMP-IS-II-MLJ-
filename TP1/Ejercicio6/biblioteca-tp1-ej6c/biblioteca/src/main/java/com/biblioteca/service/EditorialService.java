package com.biblioteca.service;

import com.biblioteca.model.Editorial;
import com.biblioteca.repository.EditorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorialService {

    private final EditorialRepository editorialRepository;

    public List<Editorial> listarActivas() {
        return editorialRepository.findAll().stream().filter(Editorial::isAlta).toList();
    }

    public Editorial buscarPorId(Long id) {
        return editorialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada: " + id));
    }

    public Editorial guardar(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    public void darDeBaja(Long id) {
        Editorial editorial = buscarPorId(id);
        editorial.setAlta(false);
        editorialRepository.save(editorial);
    }
}
