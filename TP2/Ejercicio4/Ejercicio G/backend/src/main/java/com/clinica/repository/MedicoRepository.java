package com.clinica.repository;

import com.clinica.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String> {
    Optional<Medico> findByDocumento(String documento);
    boolean existsByDocumento(String documento);
}
