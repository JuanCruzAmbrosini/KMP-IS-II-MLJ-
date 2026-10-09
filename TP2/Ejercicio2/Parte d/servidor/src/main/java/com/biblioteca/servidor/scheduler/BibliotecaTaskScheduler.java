package com.biblioteca.servidor.scheduler;

import com.biblioteca.servidor.model.EmailLog;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.PersonaRepository;
import com.biblioteca.servidor.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BibliotecaTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaTaskScheduler.class);

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Tarea programada diaria: Envío de correos automáticos 1 día antes del vencimiento del libro.
     * Expresión Cron configurable en application.properties (por defecto todos los días a las 08:00 AM).
     */
    @Scheduled(cron = "${biblioteca.scheduling.cron.recordatorios:0 0 8 * * ?}")
    public List<EmailLog> verificarRecordatoriosVencimientos() {
        log.info("⏰ [SCHEDULER] Ejecutando tarea programada: Verificación de préstamos por vencer en 24h...");
        LocalDate manana = LocalDate.now().plusDays(1);
        List<EmailLog> logs = new ArrayList<>();

        List<Persona> personas = personaRepository.findAll();
        for (Persona persona : personas) {
            if (persona.getEmail() == null || persona.getEmail().isBlank()) {
                continue;
            }
            if (persona.getLibros() != null) {
                for (Libro libro : persona.getLibros()) {
                    if (libro.getFechaVencimientoDevolucion() != null 
                            && libro.getFechaVencimientoDevolucion().isEqual(manana)) {
                        log.info("📬 Enviando recordatorio de vencimiento a {} para el libro '{}' (Vence: {})",
                                persona.getEmail(), libro.getTitulo(), libro.getFechaVencimientoDevolucion());
                        EmailLog emailLog = emailService.enviarRecordatorioVencimiento(persona, libro);
                        logs.add(emailLog);
                    }
                }
            }
        }

        log.info("⏰ [SCHEDULER] Tarea finalizada. Total recordatorios de vencimiento enviados: {}", logs.size());
        return logs;
    }

    /**
     * Tarea programada diaria: Envío de salutaciones por correo el día de cumpleaños con HTML y botón a la facultad.
     * Expresión Cron configurable en application.properties (por defecto todos los días a las 08:00 AM).
     */
    @Scheduled(cron = "${biblioteca.scheduling.cron.cumpleanios:0 0 8 * * ?}")
    public List<EmailLog> verificarCumpleanios() {
        log.info("🎂 [SCHEDULER] Ejecutando tarea programada: Verificación de cumpleaños del día...");
        LocalDate hoy = LocalDate.now();
        List<EmailLog> logs = new ArrayList<>();

        List<Persona> personas = personaRepository.findAll();
        for (Persona persona : personas) {
            if (persona.getEmail() == null || persona.getEmail().isBlank()) {
                continue;
            }
            if (persona.getFechaNacimiento() != null) {
                if (persona.getFechaNacimiento().getMonth() == hoy.getMonth()
                        && persona.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth()) {
                    log.info("🎉 ¡Cumpleaños detectado! Enviando salutación HTML a {} ({})",
                            persona.getNombre(), persona.getEmail());
                    EmailLog emailLog = emailService.enviarSalutacionCumpleanios(persona);
                    logs.add(emailLog);
                }
            }
        }

        log.info("🎂 [SCHEDULER] Tarea finalizada. Total felicitaciones de cumpleaños enviadas: {}", logs.size());
        return logs;
    }
}
