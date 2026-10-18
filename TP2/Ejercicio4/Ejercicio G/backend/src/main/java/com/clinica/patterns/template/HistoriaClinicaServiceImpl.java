package com.clinica.patterns.template;

import com.clinica.model.HistoriaClinica;
import com.clinica.repository.HistoriaClinicaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoriaClinicaServiceImpl extends AbstractCrudService<HistoriaClinica, String> {

    private final HistoriaClinicaRepository historiaClinicaRepository;

    public HistoriaClinicaServiceImpl(HistoriaClinicaRepository historiaClinicaRepository) {
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    @Override
    protected JpaRepository<HistoriaClinica, String> getRepository() {
        return historiaClinicaRepository;
    }

    @Override
    protected void validarReglasDeNegocio(HistoriaClinica entidad) {
        if (entidad.getPaciente() == null) {
            throw new IllegalArgumentException("La historia clínica debe estar asociada a un Paciente válido.");
        }
        if (entidad.getUsuario() == null) {
            throw new IllegalArgumentException("La historia clínica debe registrar al Usuario responsable de su creación.");
        }
    }

    @Override
    protected void preGuardado(HistoriaClinica entidad) {
        // Asegurar la integridad referencial de los detalles antes de persistir
        if (entidad.getDetalles() != null) {
            entidad.getDetalles().forEach(detalle -> detalle.setHistoriaClinica(entidad));
        }
    }

    @Override
    protected void postGuardado(HistoriaClinica entidadPersistida) {
        System.out.println("[AUDITORÍA] Historia clínica actualizada para el paciente: " 
                + entidadPersistida.getPaciente().getNombre() + " " + entidadPersistida.getPaciente().getApellido()
                + " con " + entidadPersistida.getDetalles().size() + " entradas médicas.");
    }

    public List<HistoriaClinica> buscarPorPaciente(String pacienteId) {
        return historiaClinicaRepository.findByPacienteIdWithDetalles(pacienteId);
    }
}
