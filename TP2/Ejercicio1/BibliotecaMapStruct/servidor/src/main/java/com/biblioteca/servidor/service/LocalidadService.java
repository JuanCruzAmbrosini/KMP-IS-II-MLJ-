package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.LocalidadDTO;
import com.biblioteca.servidor.mapper.LocalidadMapper;
import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalidadService {

    @Autowired
    private LocalidadRepository localidadRepository;

    @Autowired
    private LocalidadMapper localidadMapper;

    public List<LocalidadDTO> findAll() {
        List<Localidad> entidades = localidadRepository.findAll();
        return localidadMapper.toDTOList(entidades);
    }

    public Optional<LocalidadDTO> findById(Long id) {
        return localidadRepository.findById(id)
                .map(localidadMapper::toDTO);
    }

    public LocalidadDTO save(LocalidadDTO dto) {
        Localidad entidad = localidadMapper.toEntity(dto);
        entidad.setId(null);
        Localidad guardada = localidadRepository.save(entidad);
        return localidadMapper.toDTO(guardada);
    }

    public LocalidadDTO update(Long id, LocalidadDTO updatedDto) {
        return localidadRepository.findById(id)
                .map(loc -> {
                    loc.setDenominacion(updatedDto.getDenominacion());
                    Localidad guardada = localidadRepository.save(loc);
                    return localidadMapper.toDTO(guardada);
                })
                .orElseThrow(() -> new RuntimeException("Localidad no encontrada con id: " + id));
    }

    public void deleteById(Long id) {
        localidadRepository.deleteById(id);
    }
}
