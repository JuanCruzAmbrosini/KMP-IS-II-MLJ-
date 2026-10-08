package com.biblioteca.servidor.repository;

import com.biblioteca.servidor.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByArchivoPdf(String archivoPdf);
}
