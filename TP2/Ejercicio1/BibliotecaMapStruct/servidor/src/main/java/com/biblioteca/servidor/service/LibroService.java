package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.mapper.LibroMapper;
import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.repository.AutorRepository;
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
    private AutorRepository autorRepository;

    @Autowired
    private LibroMapper libroMapper;

    public List<LibroDTO> findAll() {
        List<Libro> entidades = libroRepository.findAll();
        return libroMapper.toDTOList(entidades);
    }

    public Optional<LibroDTO> findById(Long id) {
        return libroRepository.findById(id)
                .map(libroMapper::toDTO);
    }

    public LibroDTO save(LibroDTO dto) {
        Libro entidad = libroMapper.toEntity(dto);
        entidad.setId(null);
        if (dto.getAutores() != null) {
            List<Autor> managedAutores = dto.getAutores().stream()
                    .filter(a -> a != null && a.getId() != null)
                    .map(a -> autorRepository.findById(a.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            entidad.setAutores(new ArrayList<>(managedAutores));
        } else {
            entidad.setAutores(new ArrayList<>());
        }
        Libro guardado = libroRepository.save(entidad);
        return libroMapper.toDTO(guardado);
    }

    public LibroDTO update(Long id, LibroDTO updatedDto) {
        return libroRepository.findById(id)
                .map(lib -> {
                    lib.setTitulo(updatedDto.getTitulo());
                    lib.setFecha(updatedDto.getFecha());
                    lib.setGenero(updatedDto.getGenero());
                    lib.setPaginas(updatedDto.getPaginas());
                    lib.setAutor(updatedDto.getAutor());
                    if (updatedDto.getAutores() != null) {
                        List<Autor> managedAutores = updatedDto.getAutores().stream()
                                .filter(a -> a != null && a.getId() != null)
                                .map(a -> autorRepository.findById(a.getId()).orElse(null))
                                .filter(java.util.Objects::nonNull)
                                .toList();
                        lib.setAutores(new ArrayList<>(managedAutores));
                    } else {
                        lib.setAutores(new ArrayList<>());
                    }
                    Libro guardado = libroRepository.save(lib);
                    return libroMapper.toDTO(guardado);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        libroRepository.deleteById(id);
    }
}
