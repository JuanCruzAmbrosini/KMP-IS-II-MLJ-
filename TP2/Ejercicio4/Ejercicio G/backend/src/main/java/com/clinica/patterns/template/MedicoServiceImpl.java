package com.clinica.patterns.template;

import com.clinica.model.Medico;
import com.clinica.repository.MedicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicoServiceImpl extends AbstractCrudService<Medico, String> {

    private final MedicoRepository medicoRepository;

    public MedicoServiceImpl(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Override
    protected JpaRepository<Medico, String> getRepository() {
        return medicoRepository;
    }

    @Override
    protected void validarReglasDeNegocio(Medico entidad) {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del médico es obligatorio.");
        }
        if (entidad.getApellido() == null || entidad.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del médico es obligatorio.");
        }
        if (entidad.getDocumento() == null || entidad.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento/matrícula del médico es obligatorio.");
        }

        Optional<Medico> existente = medicoRepository.findByDocumento(entidad.getDocumento().trim());
        if (existente.isPresent() && !existente.get().getId().equals(entidad.getId())) {
            throw new IllegalStateException("Ya existe un médico registrado con el documento: " + entidad.getDocumento());
        }
    }

    @Override
    protected void preGuardado(Medico entidad) {
        entidad.setNombre(entidad.getNombre().trim().toUpperCase());
        entidad.setApellido(entidad.getApellido().trim().toUpperCase());
        entidad.setDocumento(entidad.getDocumento().trim());
    }

    @Override
    protected void postGuardado(Medico entidadPersistida) {
        System.out.println("[AUDITORÍA] Médico persistido: " + entidadPersistida.getNombreCompleto());
    }
}
