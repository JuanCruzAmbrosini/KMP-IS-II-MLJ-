package com.clinica.repository;

import com.clinica.model.DetalleHistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleHistoriaClinicaRepository extends JpaRepository<DetalleHistoriaClinica, String> {
    List<DetalleHistoriaClinica> findByHistoriaClinicaId(String historiaClinicaId);
    List<DetalleHistoriaClinica> findByMedicoId(String medicoId);
}
