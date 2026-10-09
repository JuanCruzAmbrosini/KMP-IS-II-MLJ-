package com.tp2.ejercicio1.clima;

import com.tp2.ejercicio1.clima.dto.ClimaActualDTO;
import com.tp2.ejercicio1.clima.dto.PronosticoCompletoDTO;
import com.tp2.ejercicio1.clima.service.ClimaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClimaApplicationTests {

    @Autowired
    private ClimaService climaService;

    @Test
    void contextLoads() {
        assertNotNull(climaService, "El bean ClimaService debe inicializarse en el contexto de Spring");
    }

    @Test
    void testObtenerClimaActual() {
        ClimaActualDTO clima = climaService.obtenerClimaActual("Buenos Aires");
        assertNotNull(clima, "El clima no debe ser nulo");
        assertNotNull(clima.getCiudad(), "El nombre de la ciudad no debe ser nulo");
        assertNotNull(clima.getTemperatura(), "La temperatura debe estar presente");
        assertNotNull(clima.getFuente(), "La fuente debe estar indicada");
    }

    @Test
    void testObtenerPronosticoCompleto() {
        PronosticoCompletoDTO pronostico = climaService.obtenerPronosticoCompleto("Cordoba");
        assertNotNull(pronostico);
        assertNotNull(pronostico.getActual());
        assertFalse(pronostico.getDias().isEmpty(), "El pronóstico debe contener al menos 1 día");
    }
}
