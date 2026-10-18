package com.clinica.repository;

import com.clinica.model.FotoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoPacienteRepository extends JpaRepository<FotoPaciente, String> {
    List<FotoPaciente> findByPacienteId(String pacienteId);
}
