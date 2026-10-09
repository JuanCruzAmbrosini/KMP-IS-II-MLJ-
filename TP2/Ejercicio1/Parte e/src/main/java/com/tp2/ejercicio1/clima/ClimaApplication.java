package com.tp2.ejercicio1.clima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion principal Spring Boot para el Ejercicio 1 Parte e:
 * Consumo de APIs Externas mediante RestTemplate y visualizacion en FrontEnd con Bootstrap.
 */
@SpringBootApplication
public class ClimaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimaApplication.class, args);
        System.out.println("===============================================================");
        System.out.println("  ClimaHub API Externa Iniciada con Exito");
        System.out.println("  Acceda al Frontend en: http://localhost:8080/");
        System.out.println("  Endpoint API Clima:    http://localhost:8080/api/clima/actual?ciudad=Buenos+Aires");
        System.out.println("  Endpoint Pronostico:   http://localhost:8080/api/clima/pronostico?ciudad=Buenos+Aires");
        System.out.println("  Endpoint Ciudades:     http://localhost:8080/api/clima/ciudades/populares");
        System.out.println("===============================================================");
    }
}
