package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private com.biblioteca.servidor.repository.AutorRepository autorRepository;

    @Autowired
    private PdfStorageService pdfStorageService;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado con id: " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        // El PDF forma parte del libro: si se borra el libro, se borra tambien su archivo
        libroRepository.findById(id).ifPresent(libro -> {
            if (libro.tienePdf()) {
                pdfStorageService.eliminar(libro.getArchivoPdf());
            }
        });
        libroRepository.deleteById(id);
    }

    // ==================== PDF ====================

    /** Crea el libro y, si viene un archivo, lo guarda en el disco. Si algo falla se revierte todo. */
    @Transactional
    public Libro saveConPdf(Libro libro, MultipartFile archivo) {
        Libro guardado = save(libro);
        if (archivo != null && !archivo.isEmpty()) {
            adjuntarPdf(guardado, archivo);
        }
        return guardado;
    }

    /** Actualiza el libro y, si viene un archivo nuevo, reemplaza el PDF anterior. */
    @Transactional
    public Libro updateConPdf(Long id, Libro updated, MultipartFile archivo) {
        Libro actualizado = update(id, updated);
        if (archivo != null && !archivo.isEmpty()) {
            adjuntarPdf(actualizado, archivo);
        }
        return actualizado;
    }

    /** Devuelve el PDF del libro para mostrarlo en el navegador. */
    public Resource obtenerPdf(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado con id: " + id));
        if (!libro.tienePdf()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no tiene un PDF cargado");
        }
        return pdfStorageService.cargar(libro.getArchivoPdf());
    }

    private void adjuntarPdf(Libro libro, MultipartFile archivo) {
        String nuevoNombre = pdfStorageService.nombreArchivo(libro.getTitulo());

        // Dos libros distintos no pueden pisarse el mismo archivo
        libroRepository.findByArchivoPdf(nuevoNombre)
                .filter(otro -> !otro.getId().equals(libro.getId()))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ya existe un PDF llamado " + nuevoNombre + " (libro #" + otro.getId() + ")");
                });

        String nombreAnterior = libro.getArchivoPdf();
        pdfStorageService.guardar(nuevoNombre, archivo);
        libro.setArchivoPdf(nuevoNombre);
        libroRepository.save(libro);

        if (nombreAnterior != null && !nombreAnterior.equals(nuevoNombre)) {
            pdfStorageService.eliminar(nombreAnterior);
        }
    }
}
