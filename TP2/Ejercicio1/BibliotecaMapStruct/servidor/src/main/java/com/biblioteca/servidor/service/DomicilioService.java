package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.DomicilioDTO;
import com.biblioteca.servidor.mapper.DomicilioMapper;
import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.repository.DomicilioRepository;
import com.biblioteca.servidor.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomicilioService {

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Autowired
    private DomicilioMapper domicilioMapper;

    public List<DomicilioDTO> findAll() {
        List<Domicilio> entidades = domicilioRepository.findAll();
        return domicilioMapper.toDTOList(entidades);
    }

    public Optional<DomicilioDTO> findById(Long id) {
        return domicilioRepository.findById(id)
                .map(domicilioMapper::toDTO);
    }

    public DomicilioDTO save(DomicilioDTO dto) {
        Domicilio entidad = domicilioMapper.toEntity(dto);
        entidad.setId(null);
        if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
            entidad.setLocalidad(localidadRepository.findById(dto.getLocalidad().getId()).orElse(null));
        } else {
            entidad.setLocalidad(null);
        }
        Domicilio guardado = domicilioRepository.save(entidad);
        return domicilioMapper.toDTO(guardado);
    }

    public DomicilioDTO update(Long id, DomicilioDTO updatedDto) {
        return domicilioRepository.findById(id)
                .map(dom -> {
                    dom.setCalle(updatedDto.getCalle());
                    dom.setNumero(updatedDto.getNumero());
                    if (updatedDto.getLocalidad() != null && updatedDto.getLocalidad().getId() != null) {
                        dom.setLocalidad(localidadRepository.findById(updatedDto.getLocalidad().getId()).orElse(null));
                    } else {
                        dom.setLocalidad(null);
                    }
                    Domicilio guardado = domicilioRepository.save(dom);
                    return domicilioMapper.toDTO(guardado);
                })
                .orElseThrow(() -> new RuntimeException("Domicilio no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        domicilioRepository.deleteById(id);
    }
}
