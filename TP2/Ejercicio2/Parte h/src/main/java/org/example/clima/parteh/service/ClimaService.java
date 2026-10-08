package org.example.clima.parteh.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ClimaService {

    public ResponseEntity<String> getClima(String ciudad) {
        try {
            String apiKey = "appid=8ce9e8fe59a95c80e92733a490365516";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            String clima = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok(clima);

        } catch(HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al autenticar la solicitud. Verifique su clave de API y los parámetros de la solicitud.");
    }
}
