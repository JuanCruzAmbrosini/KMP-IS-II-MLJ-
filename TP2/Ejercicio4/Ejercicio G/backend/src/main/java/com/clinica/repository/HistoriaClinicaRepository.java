package com.clinica.repository;

import com.clinica.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, String> {
    
    @Query("SELECT h FROM HistoriaClinica h LEFT JOIN FETCH h.detalles d LEFT JOIN FETCH d.medico WHERE h.paciente.id = :pacienteId")
    List<HistoriaClinica> findByPacienteIdWithDetalles(@Param("pacienteId") String pacienteId);

    Optional<HistoriaClinica> findFirstByPacienteIdOrderByDetallesFechaHistoriaDesc(String pacienteId);
    
    List<HistoriaClinica> findByPacienteId(String pacienteId);
}
