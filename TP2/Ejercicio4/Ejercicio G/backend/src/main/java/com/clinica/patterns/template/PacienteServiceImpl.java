package com.clinica.patterns.template;

import com.clinica.model.Paciente;
import com.clinica.repository.PacienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteServiceImpl extends AbstractCrudService<Paciente, String> {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    protected JpaRepository<Paciente, String> getRepository() {
        return pacienteRepository;
    }

    @Override
    protected void validarReglasDeNegocio(Paciente entidad) {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio.");
        }
        if (entidad.getApellido() == null || entidad.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del paciente es obligatorio.");
        }
        if (entidad.getDocumento() == null || entidad.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento del paciente es obligatorio.");
        }

        // Regla: Documento único
        Optional<Paciente> existente = pacienteRepository.findByDocumento(entidad.getDocumento().trim());
        if (existente.isPresent() && !existente.get().getId().equals(entidad.getId())) {
            throw new IllegalStateException("Ya existe un paciente registrado con el documento: " + entidad.getDocumento());
        }
    }

    @Override
    protected void preGuardado(Paciente entidad) {
        // Normalización de datos antes de persistir
        entidad.setNombre(entidad.getNombre().trim().toUpperCase());
        entidad.setApellido(entidad.getApellido().trim().toUpperCase());
        entidad.setDocumento(entidad.getDocumento().trim());
    }

    @Override
    protected void postGuardado(Paciente entidadPersistida) {
        // Registro de auditoría o métricas
        System.out.println("[AUDITORÍA] Paciente persistido exitosamente: " + entidadPersistida.getId() + " - " + entidadPersistida.getApellido());
    }

    public Optional<Paciente> buscarPorDocumento(String documento) {
        return pacienteRepository.findByDocumento(documento);
    }
}
