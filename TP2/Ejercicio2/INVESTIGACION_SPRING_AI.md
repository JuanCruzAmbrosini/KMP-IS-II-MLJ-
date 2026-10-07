# Guía de Investigación y Práctica: Spring AI (Partes A y B)

Trabajo Práctico N° 2 - Ingeniería del Software II (Año 2026)  
**Ejercicio 2.g: Investigación Spring AI**

---

## 1. Marco Teórico de Investigación: Spring AI

### ¿Qué es Spring AI?

**Spring AI** es un proyecto oficial del ecosistema Spring Framework diseñado para aplicar los principios de diseño de Spring (portabilidad, modularidad, inyección de dependencias y abstracciones limpias) al desarrollo de aplicaciones que integran **Modelos de Lenguaje Grande (LLMs)** e Inteligencia Artificial Generativa.

Permite interactuar con múltiples proveedores de modelos fundacionales (OpenAI, Google Gemini, Anthropic Claude, Ollama, HuggingFace, etc.) utilizando una API consistente y tipada, sin acoplar la arquitectura a la biblioteca o SDK propietaria de cada proveedor.

### ¿Qué ventajas posee?

1. **Portabilidad y Desacoplamiento:** Cambiar entre proveedores (por ejemplo, migrar de OpenAI a Gemini o a un modelo local con Ollama) requiere únicamente modificar la dependencia starter y properties de configuración, manteniendo la lógica de negocio intacta.
2. **Abstracciones de Alto Nivel (`ChatClient`):** Proporciona una interfaz fluida inspirada en `RestClient` y `WebClient` para parametrizar prompts, mensajes de sistema (*System Prompts*), plantillas y configuraciones de inferencia.
3. **Manejo de Tokens y Metadatos:** Brinda acceso a `ChatResponse` con información de uso de tokens (prompt, completion y total tokens), razones de corte (`finishReason`) y metadatos del proveedor.
4. **Soporte Reactivo y Streaming:** Integración nativa con Project Reactor y Spring WebFlux (`Flux<String>`) para respuestas en tiempo real token a token vía Server-Sent Events (SSE).
5. **Ecosistema Avanzado Integrado:**
   - **RAG (Retrieval-Augmented Generation):** Abstracciones para Vector Stores (pgvector, Chroma, Milvus, Redis, etc.) y transformadores de documentos.
   - **Model Function Calling / Tools:** Capacidad de registrar funciones Java convencionales para que el LLM decida invocarlas dinámicamente cuando requiera información externa o realizar acciones en el sistema.
   - **Structured Output (Mapeo a POJOs):** Mapeo automático de respuestas generativas a clases Java fuertemente tipadas.

### Ejemplos de Aplicación en Sistemas Reales

1. **Atención al Cliente y Soporte Técnico:** Asistentes conversacionales automatizados capaces de consultar el catálogo de productos o base de conocimiento corporativa mediante RAG.
2. **Clasificación y Moderación Automática de Contenido:** Análisis de texto o imágenes en redes sociales (como publicaciones, reseñas o comentarios) detectando lenguaje inapropiado o categorizando intereses.
3. **Generación Automática de Informes y Resúmenes:** Resumir historiales médicos, actas de reuniones o generar reportes ejecutivos a partir de datos estructurados de una base de datos.
4. **Recomendación Personalizada:** Analizar preferencias y compras históricas de usuarios para recomendar productos afines mediante descripciones semánticas.

---

## 2. Desarrollo Práctico: Parte A (OpenRouter / OpenAI REST)

- **Video de Referencia:** [YouTube: Introducción Spring IA (PARTE A)](https://www.youtube.com/watch?v=sQwpfrBVYEU)  
- **Repositorio Clonado:** [github.com/todocodeacademy/cursosIA](https://github.com/todocodeacademy/cursosIA/) (`cursosIA/Clase X - EjOpenRouter`)

### Arquitectura de la Solución

En esta primera aproximación didáctica, se consume la API de modelos de IA a través de **OpenRouter** (intermediario que permite acceder a modelos como Claude, Llama 3 o GPT gratis o con bajo costo) mediante el nuevo cliente HTTP nativo de Spring Boot: `RestClient`.

### Código Implementado

- **`application.properties`**:

```properties
spring.application.name=EjOpenRouter
openrouter.api-key=${OPENROUTER_API_KEY}
openrouter.base-url=${OPENROUTER_BASE_URL:https://openrouter.ai/api/v1}
openrouter.model=${OPENROUTER_MODEL:openrouter/free}
server.port=9090
```

- **`IAController.java`**:

```java
package com.todocodeacademy.EjOpenRouter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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
                    { "role": "user", "content": "%s" }
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
```

---

## 3. Desarrollo Práctico: Parte B (Spring AI + Google Gemini)

- **Video de Referencia:** [YouTube: Spring AI + Gemini](https://www.youtube.com/watch?v=y1PSwxntY6A)
- **Ubicación del Proyecto en este Repositorio:** `c:\Users\Lucho\Desktop\KMP-IS-II-MLJ-\TP2\Ejercicio2\SpringAI-Gemini\`

### Componentes Clave Desarrollados

1. **Configuración de Dependencias (`pom.xml`):**
   - Incorporación del BOM de Spring AI (`spring-ai-bom:1.0.0-M3`).
   - Starter de Gemini (`spring-ai-vertex-ai-gemini-spring-boot-starter`).
   - Starter de Spring WebFlux (`spring-boot-starter-webflux`) para Streaming reactivo.

2. **Capa de Servicio (`GeminiService.java`):**
   - Configuración del cliente fluido `ChatClient`.
   - Inyección de identidad con **System Prompt** predeterminado: *"Eres un asistente virtual experto y amigable en desarrollo de software..."*.
   - Métodos para respuestas simples en texto, respuestas detalladas con métricas de tokens (`ChatResponse`) y flujo continuo de tokens (`Flux<String>`).

3. **Capa de Controlador REST (`GeminiController.java`):**
   - `GET /api/gemini/chat?mensaje=...`: Respuesta estándar bloqueante.
   - `GET /api/gemini/chat/detallado?mensaje=...`: Inspección de tokens y metadatos.
   - `GET /api/gemini/stream?mensaje=...`: Endpoint reactivo con tipo `text/event-stream`.

### Instrucciones de Ejecución

1. Configurar las variables de entorno para Google Cloud o Vertex AI:

   ```powershell
   $env:GOOGLE_CLOUD_PROJECT_ID="tu-proyecto-gcp"
   $env:GOOGLE_CLOUD_LOCATION="us-central1"
   ```

2. Ejecutar la aplicación:

   ```powershell
   cd SpringAI-Gemini
   .\mvnw.cmd spring-boot:run
   ```

3. Probar los endpoints en el navegador o Postman:
   - `http://localhost:8080/api/gemini/chat?mensaje=¿Que+es+Spring+AI?`
   - `http://localhost:8080/api/gemini/stream?mensaje=Explica+el+patron+Template+Method`
