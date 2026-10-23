package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.EmailLog;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.PersonaRepository;
import com.biblioteca.servidor.scheduler.BibliotecaTaskScheduler;
import com.biblioteca.servidor.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private BibliotecaTaskScheduler scheduler;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping("/historial")
    public List<EmailLog> getHistorial() {
        return emailService.obtenerHistorial();
    }

    @PostMapping("/ejecutar-vencimientos")
    public ResponseEntity<Map<String, Object>> ejecutarVencimientos() {
        List<EmailLog> enviados = scheduler.verificarRecordatoriosVencimientos();
        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje", "Tarea de vencimientos ejecutada con éxito");
        resp.put("totalEnviados", enviados.size());
        resp.put("detalles", enviados);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/ejecutar-cumpleanios")
    public ResponseEntity<Map<String, Object>> ejecutarCumpleanios() {
        List<EmailLog> enviados = scheduler.verificarCumpleanios();
        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje", "Tarea de cumpleaños ejecutada con éxito");
        resp.put("totalEnviados", enviados.size());
        resp.put("detalles", enviados);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/resumen-hoy")
    public ResponseEntity<Map<String, Object>> getResumenHoy() {
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        List<Map<String, Object>> cumpleaneros = new ArrayList<>();
        List<Map<String, Object>> prestamosPorVencer = new ArrayList<>();

        for (Persona p : personaRepository.findAll()) {
            if (p.getFechaNacimiento() != null 
                    && p.getFechaNacimiento().getMonth() == hoy.getMonth()
                    && p.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth()) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", p.getId());
                c.put("nombre", p.getNombre() + " " + p.getApellido());
                c.put("email", p.getEmail());
                c.put("fechaNacimiento", p.getFechaNacimiento().toString());
                cumpleaneros.add(c);
            }
            if (p.getLibros() != null) {
                for (Libro l : p.getLibros()) {
                    if (l.getFechaVencimientoDevolucion() != null 
                            && l.getFechaVencimientoDevolucion().isEqual(manana)) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("personaId", p.getId());
                        item.put("personaNombre", p.getNombre() + " " + p.getApellido());
                        item.put("personaEmail", p.getEmail());
                        item.put("libroId", l.getId());
                        item.put("libroTitulo", l.getTitulo());
                        item.put("fechaVencimiento", l.getFechaVencimientoDevolucion().toString());
                        prestamosPorVencer.add(item);
                    }
                }
            }
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("fechaHoy", hoy.toString());
        resumen.put("fechaManana", manana.toString());
        resumen.put("cumpleanerosHoy", cumpleaneros);
        resumen.put("prestamosPorVencerManana", prestamosPorVencer);
        return ResponseEntity.ok(resumen);
    }
}
