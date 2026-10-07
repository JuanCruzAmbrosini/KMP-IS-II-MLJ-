package com.example.springai.gemini.controller;

import com.example.springai.gemini.service.GeminiService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * Endpoint básico: Retorna la respuesta completa generada por Gemini
     * GET /api/gemini/chat?mensaje=Explica+que+es+Spring+AI
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "Hola, cuentame que puedes hacer.") String mensaje) {
        return geminiService.generarRespuesta(mensaje);
    }

    /**
     * Endpoint con métricas: Retorna información sobre tokens consumidos y metadatos
     * GET /api/gemini/chat/detallado?mensaje=Dame+un+ejemplo+de+microservicios
     */
    @GetMapping("/chat/detallado")
    public ChatResponse chatDetallado(@RequestParam String mensaje) {
        return geminiService.generarRespuestaDetallada(mensaje);
    }

    /**
     * Endpoint reactivo Streaming: Retorna eventos Server-Sent Events (SSE) a medida que la IA genera tokens
     * GET /api/gemini/stream?mensaje=Escribe+un+ensayo+breve+sobre+Spring+Boot
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String mensaje) {
        return geminiService.generarRespuestaStream(mensaje);
    }
}

