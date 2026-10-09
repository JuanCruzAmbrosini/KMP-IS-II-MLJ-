package com.tp2.ejercicio1.clima.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuracion del bean RestTemplate utilizado por el Backend para consumir APIs Externas.
 * Permite definir tiempos de conexion y lectura, asi como interceptores o convertidores si fueran necesarios.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${clima.http.connect-timeout:6000}")
    private int connectTimeout;

    @Value("${clima.http.read-timeout:6000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
