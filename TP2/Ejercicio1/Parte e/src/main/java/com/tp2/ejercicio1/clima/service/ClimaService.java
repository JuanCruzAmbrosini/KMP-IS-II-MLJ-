package com.tp2.ejercicio1.clima.service;

import com.tp2.ejercicio1.clima.dto.ApiEstadoDTO;
import com.tp2.ejercicio1.clima.dto.CiudadBusquedaDTO;
import com.tp2.ejercicio1.clima.dto.ClimaActualDTO;
import com.tp2.ejercicio1.clima.dto.PronosticoCompletoDTO;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de clima consumiendo APIs externas.
 */
public interface ClimaService {

    /**
     * Obtiene el clima actual para una ciudad especifica.
     */
    ClimaActualDTO obtenerClimaActual(String nombreCiudad);

    /**
     * Obtiene el clima actual y el pronostico extendido para los proximos dias.
     */
    PronosticoCompletoDTO obtenerPronosticoCompleto(String nombreCiudad);

    /**
     * Busca sugerencias de ciudades coincidentes con el texto ingresado.
     */
    List<CiudadBusquedaDTO> buscarCiudades(String query);

    /**
     * Obtiene un resumen climatico en tiempo real de un conjunto de ciudades representativas.
     */
    List<ClimaActualDTO> obtenerCiudadesPopulares();

    /**
     * Verifica la disponibilidad y estado de latencia de la API meteorologica externa.
     */
    ApiEstadoDTO verificarEstadoApi();
}
