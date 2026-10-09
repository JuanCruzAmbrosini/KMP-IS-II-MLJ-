package com.tp2.ejercicio1.clima.controller;

import com.tp2.ejercicio1.clima.dto.*;
import com.tp2.ejercicio1.clima.service.ClimaService;
import com.tp2.ejercicio1.clima.service.WhatsAppMockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST que expone los servicios meteorologicos y la demostracion de WhatsApp
 * para ser consumidos por el FrontEnd basado en Bootstrap.
 */
@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "*")
public class ClimaController {

    private final ClimaService climaService;
    private final WhatsAppMockService whatsAppMockService;

    public ClimaController(ClimaService climaService, WhatsAppMockService whatsAppMockService) {
        this.climaService = climaService;
        this.whatsAppMockService = whatsAppMockService;
    }

    /**
     * Obtiene el clima actual de una ciudad especificada por parametro.
     * Ejemplo: GET /api/clima/actual?ciudad=Buenos+Aires
     */
    @GetMapping("/actual")
    public ResponseEntity<ClimaActualDTO> obtenerClimaActual(@RequestParam(defaultValue = "Buenos Aires") String ciudad) {
        ClimaActualDTO clima = climaService.obtenerClimaActual(ciudad);
        return ResponseEntity.ok(clima);
    }

    /**
     * Obtiene el clima actual y el pronostico extendido para los proximos 7 dias.
     * Ejemplo: GET /api/clima/pronostico?ciudad=Buenos+Aires
     */
    @GetMapping("/pronostico")
    public ResponseEntity<PronosticoCompletoDTO> obtenerPronostico(@RequestParam(defaultValue = "Buenos Aires") String ciudad) {
        PronosticoCompletoDTO pronostico = climaService.obtenerPronosticoCompleto(ciudad);
        return ResponseEntity.ok(pronostico);
    }

    /**
     * Permite buscar ciudades por coincidencia de texto para autocompletar.
     * Ejemplo: GET /api/clima/buscar?query=Cord
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CiudadBusquedaDTO>> buscarCiudades(@RequestParam String query) {
        List<CiudadBusquedaDTO> ciudades = climaService.buscarCiudades(query);
        return ResponseEntity.ok(ciudades);
    }

    /**
     * Retorna una lista con el clima de ciudades principales para las tarjetas del dashboard.
     * Ejemplo: GET /api/clima/ciudades/populares
     */
    @GetMapping("/ciudades/populares")
    public ResponseEntity<List<ClimaActualDTO>> obtenerCiudadesPopulares() {
        List<ClimaActualDTO> populares = climaService.obtenerCiudadesPopulares();
        return ResponseEntity.ok(populares);
    }

    /**
     * Retorna el estado de disponibilidad y tiempo de respuesta de la API externa.
     * Ejemplo: GET /api/clima/estado
     */
    @GetMapping("/estado")
    public ResponseEntity<ApiEstadoDTO> verificarEstado() {
        ApiEstadoDTO estado = climaService.verificarEstadoApi();
        return ResponseEntity.ok(estado);
    }

    /**
     * Simula el envio de una notificacion meteorologica a traves de WhatsApp Business Cloud API.
     * Ejemplo: POST /api/clima/whatsapp/simular
     */
    @PostMapping("/whatsapp/simular")
    public ResponseEntity<WhatsAppSimulacionDTO> simularEnvioWhatsApp(@RequestBody Map<String, String> payload) {
        String telefono = payload.getOrDefault("telefono", "5491144445555");
        String ciudad = payload.getOrDefault("ciudad", "Buenos Aires");
        String temp = payload.getOrDefault("temperatura", "20.5°C");
        String cond = payload.getOrDefault("condicion", "Cielo despejado");

        WhatsAppSimulacionDTO res = whatsAppMockService.simularEnvioPlantilla(telefono, ciudad, temp, cond);
        return ResponseEntity.ok(res);
    }
}
