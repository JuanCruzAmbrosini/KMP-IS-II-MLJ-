package com.example.springai.gemini.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("Eres un asistente virtual experto y amigable en desarrollo de software, arquitectura de sistemas y tecnología.")
                .build();
    }

    /**
     * Consulta simple: recibe un prompt y retorna el texto generado por Gemini.
     */
    public String generarRespuesta(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * Consulta con metadatos: retorna ChatResponse para inspeccionar tokens usados,
     * modelo y detalles de finalización.
     */
    public ChatResponse generarRespuestaDetallada(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();
    }

    /**
     * Respuesta reactiva en Streaming (tiempo real) utilizando Flux de Spring WebFlux.
     */
    public Flux<String> generarRespuestaStream(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}

