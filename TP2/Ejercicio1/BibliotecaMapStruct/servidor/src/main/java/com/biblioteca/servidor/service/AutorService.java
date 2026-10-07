package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.mapper.AutorMapper;
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

    @Autowired
    private AutorMapper autorMapper;

    public List<AutorDTO> findAll() {
        List<Autor> entidades = autorRepository.findAll();
        return autorMapper.toDTOList(entidades);
    }

    public Optional<AutorDTO> findById(Long id) {
        return autorRepository.findById(id)
                .map(autorMapper::toDTO);
    }

    public AutorDTO save(AutorDTO dto) {
        Autor entidad = autorMapper.toEntity(dto);
        entidad.setId(null);
        Autor guardado = autorRepository.save(entidad);
        return autorMapper.toDTO(guardado);
    }

    public AutorDTO update(Long id, AutorDTO updatedDto) {
        return autorRepository.findById(id)
                .map(a -> {
                    a.setNombre(updatedDto.getNombre());
                    a.setApellido(updatedDto.getApellido());
                    a.setBiografia(updatedDto.getBiografia());
                    Autor guardado = autorRepository.save(a);
                    return autorMapper.toDTO(guardado);
                })
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        autorRepository.deleteById(id);
    }
}
