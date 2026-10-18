package com.clinica.patterns.facade;

import com.clinica.model.FotoPaciente;
import com.clinica.model.HistoriaClinica;
import com.clinica.model.Medico;
import com.clinica.model.Paciente;

import java.util.List;
import java.util.Optional;

/**
 * PATRÓN ESTRUCTURAL: Facade
 * 
 * Interfaz unificada de alto nivel que oculta la complejidad y orquestación
 * de los subsistemas de Pacientes, Fotos, Historias Clínicas, Médicos y Notificaciones.
 */
public interface ClinicaFacade {

    Paciente registrarPacienteConFoto(PacienteRegistroDTO dto, byte[] fotoContenido, String nombreArchivo, String mime);

    HistoriaClinica agregarDetalleAHistoria(RegistrarDetalleDTO dto);

    List<HistoriaClinica> obtenerHistoriasPorPaciente(String pacienteId);

    List<Paciente> listarPacientes();

    Optional<Paciente> buscarPacientePorId(String id);

    List<Medico> listarMedicos();

    Optional<Medico> buscarMedicoPorId(String id);

    Optional<FotoPaciente> obtenerFotoPorId(String id);

    void eliminarPaciente(String id);
}
