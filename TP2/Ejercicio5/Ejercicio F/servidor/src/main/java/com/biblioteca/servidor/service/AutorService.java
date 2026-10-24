package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> findAll() {
        return autorRepository.findAll();
    }

    public Optional<Autor> findById(Long id) {
        return autorRepository.findById(id);
    }

    public Autor save(Autor autor) {
        return autorRepository.save(autor);
    }

    public Autor update(Long id, Autor updated) {
        return autorRepository.findById(id)
                .map(a -> {
                    a.setNombre(updated.getNombre());
                    a.setApellido(updated.getApellido());
                    a.setBiografia(updated.getBiografia());
                    return autorRepository.save(a);
                })
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        autorRepository.deleteById(id);
    }
}

