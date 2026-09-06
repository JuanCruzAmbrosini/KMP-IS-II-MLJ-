package com.biblioteca.service;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    public List<Prestamo> listarActivos() {
        return prestamoRepository.findAll().stream().filter(Prestamo::isAlta).toList();
    }

    public List<Prestamo> listarPorUsuario(String mail) {
        return prestamoRepository.findByUsuarioMail(mail);
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prestamo no encontrado: " + id));
    }

    // Registrar un prestamo nuevo controla stock y actualiza contadores del libro
    @Transactional
    public Prestamo registrarPrestamo(Prestamo prestamo) {
        Libro libro = libroRepository.findById(prestamo.getLibro().getId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (libro.getEjemplaresRestantes() <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles de: " + libro.getTitulo());
        }

        prestamo.setFechaPrestamo(LocalDate.now());
        libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
        libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    // Registrar devolucion libera el ejemplar
    @Transactional
    public void registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = buscarPorId(prestamoId);
        prestamo.setFechaDevolucion(LocalDate.now());

        Libro libro = prestamo.getLibro();
        libro.setEjemplaresPrestados(Math.max(0, libro.getEjemplaresPrestados() - 1));
        libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);

        libroRepository.save(libro);
        prestamoRepository.save(prestamo);
    }
}
