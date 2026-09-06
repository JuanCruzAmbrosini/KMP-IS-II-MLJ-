package com.biblioteca.repository;

import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuario(Usuario usuario);
    List<Prestamo> findByUsuarioMail(String mail);
}
