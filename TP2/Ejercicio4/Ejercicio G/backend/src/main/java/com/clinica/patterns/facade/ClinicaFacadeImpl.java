package com.clinica.patterns.facade;

import com.clinica.model.*;
import com.clinica.patterns.builder.HistoriaClinicaBuilder;
import com.clinica.patterns.observer.HistoriaObservable;
import com.clinica.patterns.observer.NuevoDetalleHistoriaEvent;
import com.clinica.patterns.template.HistoriaClinicaServiceImpl;
import com.clinica.patterns.template.MedicoServiceImpl;
import com.clinica.patterns.template.PacienteServiceImpl;
import com.clinica.repository.FotoPacienteRepository;
import com.clinica.repository.HistoriaClinicaRepository;
import com.clinica.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la Fachada (Facade)
 * Coordina los servicios basados en Template Method, la construcción mediante Builder,
 * y la emisión de notificaciones a través del patrón Observer.
 */
@Service
public class ClinicaFacadeImpl implements ClinicaFacade {

    private final PacienteServiceImpl pacienteService;
    private final MedicoServiceImpl medicoService;
    private final HistoriaClinicaServiceImpl historiaClinicaService;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final FotoPacienteRepository fotoPacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoriaObservable historiaObservable;

    public ClinicaFacadeImpl(
            PacienteServiceImpl pacienteService,
            MedicoServiceImpl medicoService,
            HistoriaClinicaServiceImpl historiaClinicaService,
            HistoriaClinicaRepository historiaClinicaRepository,
            FotoPacienteRepository fotoPacienteRepository,
            UsuarioRepository usuarioRepository,
            HistoriaObservable historiaObservable) {
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.historiaClinicaService = historiaClinicaService;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.fotoPacienteRepository = fotoPacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.historiaObservable = historiaObservable;
    }

    @Override
    @Transactional
    public Paciente registrarPacienteConFoto(PacienteRegistroDTO dto, byte[] fotoContenido, String nombreArchivo, String mime) {
        // 1. Crear instancia de Paciente
        Paciente paciente = new Paciente(dto.getNombre(), dto.getApellido(), dto.getDocumento());

        // 2. Si hay foto, adjuntarla a la entidad
        if (fotoContenido != null && fotoContenido.length > 0) {
            FotoPaciente foto = new FotoPaciente(
                    nombreArchivo != null ? nombreArchivo : "perfil.jpg",
                    mime != null ? mime : "image/jpeg",
                    fotoContenido,
                    paciente
            );
            paciente.addFoto(foto);
        }

        // 3. Persistir usando el Template Method del PacienteService
        return pacienteService.guardar(paciente);
    }

    @Override
    @Transactional
    public HistoriaClinica agregarDetalleAHistoria(RegistrarDetalleDTO dto) {
        // 1. Obtener Paciente
        Paciente paciente = pacienteService.buscarPorId(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + dto.getPacienteId()));

        // 2. Obtener Médico
        Medico medico = medicoService.buscarPorId(dto.getMedicoId())
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado con ID: " + dto.getMedicoId()));

        // 3. Obtener Usuario operador (o crear uno por defecto si no existe)
        Usuario usuario = null;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);
        }
        if (usuario == null) {
            usuario = usuarioRepository.findAll().stream().findFirst()
                    .orElseGet(() -> usuarioRepository.save(new Usuario("admin_clinica", "AdminPass123!")));
        }

        // 4. Buscar si el paciente ya tiene historia clínica abierta o construir una nueva
        List<HistoriaClinica> historias = historiaClinicaRepository.findByPacienteIdWithDetalles(paciente.getId());
        HistoriaClinica historia;
        Date fechaRegistro = dto.getFechaHistoria() != null ? dto.getFechaHistoria() : new Date();

        if (historias.isEmpty()) {
            // PATRÓN BUILDER: Construir la HistoriaClinica inicial de forma fluida y consistente
            historia = HistoriaClinicaBuilder.builder()
                    .conPaciente(paciente)
                    .creadoPor(usuario)
                    .agregarDetalle(medico, dto.getDetalleHistoria(), fechaRegistro)
                    .build();
        } else {
            // Historia ya existente: se agrega nuevo detalle
            historia = historias.get(0);
            DetalleHistoriaClinica nuevoDetalle = new DetalleHistoriaClinica(fechaRegistro, dto.getDetalleHistoria(), medico);
            historia.addDetalle(nuevoDetalle);
        }

        // 5. Persistir usando el Template Method de HistoriaClinicaService
        HistoriaClinica guardada = historiaClinicaService.guardar(historia);

        // 6. PATRÓN OBSERVER: Notificar a los suscriptores (médico, alertas) del nuevo evento clínico
        NuevoDetalleHistoriaEvent evento = new NuevoDetalleHistoriaEvent(
                paciente, medico, dto.getDetalleHistoria(), fechaRegistro
        );
        historiaObservable.notificarObservadores(evento);

        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinica> obtenerHistoriasPorPaciente(String pacienteId) {
        return historiaClinicaRepository.findByPacienteIdWithDetalles(pacienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarPacientes() {
        return pacienteService.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPacientePorId(String id) {
        return pacienteService.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> listarMedicos() {
        return medicoService.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> buscarMedicoPorId(String id) {
        return medicoService.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FotoPaciente> obtenerFotoPorId(String id) {
        return fotoPacienteRepository.findById(id);
    }

    @Override
    @Transactional
    public void eliminarPaciente(String id) {
        pacienteService.eliminar(id);
    }
}
