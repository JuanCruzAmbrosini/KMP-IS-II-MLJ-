package com.colmena.videojuegos.repositories;

import com.colmena.videojuegos.entities.AuditoriaVideojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioAuditoriaVideojuego extends JpaRepository<AuditoriaVideojuego, Long> {
    List<AuditoriaVideojuego> findAllByOrderByFechaDescIdDesc();
}