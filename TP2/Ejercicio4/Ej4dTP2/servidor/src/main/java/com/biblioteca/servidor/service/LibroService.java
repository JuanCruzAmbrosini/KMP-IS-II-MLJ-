package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroDigital;
import com.biblioteca.servidor.model.LibroFisico;
import com.biblioteca.servidor.pattern.behavioral.iterator.ColeccionLibros;
import com.biblioteca.servidor.pattern.behavioral.iterator.ColeccionLibrosImpl;
import com.biblioteca.servidor.pattern.behavioral.iterator.LibroIterator;
import com.biblioteca.servidor.pattern.behavioral.strategy.BusquedaLibroContext;
import com.biblioteca.servidor.pattern.creational.factory.LibroFactoryProvider;
import com.biblioteca.servidor.pattern.structural.adapter.LibroAdapter;
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

    @Autowired
    private BusquedaLibroContext busquedaLibroContext;

    @Autowired
    private LibroFactoryProvider libroFactoryProvider;

    @Autowired
    private LibroAdapter libroAdapter;

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
                    .map(a -> {
                        if (a.getId() != null) {
                            return autorRepository.findById(a.getId()).orElse(null);
                        }
                        return autorRepository.save(a);
                    })
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

        // Actualización de atributos específicos para LibroFisico
        if (existente instanceof LibroFisico fisicoExistente && nuevo instanceof LibroFisico fisicoNuevo) {
            if (fisicoNuevo.getNumeroEstanteria() != null) {
                fisicoExistente.setNumeroEstanteria(fisicoNuevo.getNumeroEstanteria());
            }
            if (fisicoNuevo.getUbicacion() != null) {
                fisicoExistente.setUbicacion(fisicoNuevo.getUbicacion());
            }
        }

        // Actualización de atributos específicos para LibroDigital
        if (existente instanceof LibroDigital digitalExistente && nuevo instanceof LibroDigital digitalNuevo) {
            if (digitalNuevo.getFormato() != null) {
                digitalExistente.setFormato(digitalNuevo.getFormato());
            }
            if (digitalNuevo.getTamanioMB() != null) {
                digitalExistente.setTamanioMB(digitalNuevo.getTamanioMB());
            }
            if (digitalNuevo.getUrlDescarga() != null) {
                digitalExistente.setUrlDescarga(digitalNuevo.getUrlDescarga());
            }
        }

        if (nuevo.getAutores() != null) {
            List<Autor> managedAutores = nuevo.getAutores().stream()
                    .map(a -> {
                        if (a.getId() != null) {
                            return autorRepository.findById(a.getId()).orElse(null);
                        }
                        return autorRepository.save(a);
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
            existente.setAutores(new ArrayList<>(managedAutores));
        } else {
            existente.setAutores(new ArrayList<>());
        }
    }

    /**
     * Patrón Creacional Fábrica + Builder + Estructural Adapter:
     * Crea un libro polimórfico (físico o digital) a partir de su DTO.
     */
    public Libro crearDesdeDto(LibroDTO dto) {
        Libro libro = libroAdapter.toEntity(dto);
        return save(libro);
    }

    /**
     * Patrón Comportamiento Strategy:
     * Busca libros utilizando dinámicamente la estrategia indicada ("TITULO", "GENERO", "AUTOR").
     */
    public List<Libro> buscarConEstrategia(String tipoEstrategia, String criterio) {
        List<Libro> universo = findAll();
        return busquedaLibroContext.ejecutarCon(tipoEstrategia, universo, criterio);
    }

    /**
     * Patrón Comportamiento Iterator:
     * Recorre la colección de libros utilizando un LibroIterator para obtener aquellos
     * que pertenecen a un determinado autor (por nombre/apellido o autor descriptivo).
     */
    public List<Libro> filtrarPorAutorConIterador(String autor) {
        List<Libro> todos = findAll();
        ColeccionLibros coleccion = new ColeccionLibrosImpl(todos);
        LibroIterator iterator = coleccion.crearIteradorPorAutor(autor);

        List<Libro> resultado = new ArrayList<>();
        while (iterator.hasNext()) {
            resultado.add(iterator.next());
        }
        return resultado;
    }

    /**
     * Patrón Comportamiento Iterator:
     * Recorre la colección de libros utilizando un LibroIterator filtrando por el ID de Autor.
     */
    public List<Libro> filtrarPorAutorIdConIterador(Long autorId) {
        List<Libro> todos = findAll();
        ColeccionLibros coleccion = new ColeccionLibrosImpl(todos);
        LibroIterator iterator = coleccion.crearIteradorPorAutorId(autorId);

        List<Libro> resultado = new ArrayList<>();
        while (iterator.hasNext()) {
            resultado.add(iterator.next());
        }
        return resultado;
    }
}
