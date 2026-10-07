package com.todocodeacademy.EjOpenRouter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class IAController {

    private final RestClient restClient;

    @Value("${openrouter.model}")
    private String model;

    public IAController(
            @Value("${openrouter.base-url}") String baseUrl,
            @Value("${openrouter.api-key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @GetMapping("/preguntar")
    public String preguntar(@RequestParam String pregunta) {

        String body = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(model, pregunta);

        return restClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(String.class);
    }

}
