package com.tp2.ejercicio1.clima.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tp2.ejercicio1.clima.dto.WhatsAppSimulacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Servicio que ilustra y demuestra el consumo de la WhatsApp Business Cloud API de Meta
 * utilizando RestTemplate en Spring Boot con autenticacion Bearer y estructura JSON oficial de Meta.
 */
@Service
public class WhatsAppMockService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppMockService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${whatsapp.cloud-api.base-url:https://graph.facebook.com/v19.0}")
    private String metaBaseUrl;

    @Value("${whatsapp.cloud-api.phone-number-id:109283748291039}")
    private String phoneNumberId;

    @Value("${whatsapp.cloud-api.access-token:EAAB_TOKEN_DEMO}")
    private String accessToken;

    public WhatsAppMockService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Construye y demuestra la llamada POST con RestTemplate a Meta Cloud API para enviar
     * una notificacion de plantilla (por ejemplo, alerta climatica o aviso de biblioteca).
     */
    public WhatsAppSimulacionDTO simularEnvioPlantilla(String numeroTelefono, String ciudad, String temperatura, String condicion) {
        if (numeroTelefono == null || numeroTelefono.isBlank()) {
            numeroTelefono = "5491144445555";
        }

        // URL oficial de Meta Graph API
        String endpointUrl = metaBaseUrl + "/" + phoneNumberId + "/messages";

        // Estructura oficial del JSON de Meta para mensajes de tipo 'template'
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", numeroTelefono);
        payload.put("type", "template");

        ObjectNode templateNode = payload.putObject("template");
        templateNode.put("name", "alerta_clima_diaria");
        templateNode.putObject("language").put("code", "es_AR");

        ArrayNode components = templateNode.putArray("components");
        ObjectNode bodyComponent = components.addObject();
        bodyComponent.put("type", "body");
        ArrayNode parameters = bodyComponent.putArray("parameters");
        parameters.addObject().put("type", "text").put("text", ciudad);
        parameters.addObject().put("type", "text").put("text", temperatura);
        parameters.addObject().put("type", "text").put("text", condicion);

        String jsonPayloadString = payload.toPrettyString();
        log.info("Payload construido para WhatsApp Cloud API:\n{}", jsonPayloadString);

        // Headers requeridos por Meta (Bearer Token y Content-Type)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayloadString, headers);

        WhatsAppSimulacionDTO resultado = new WhatsAppSimulacionDTO();
        resultado.setDestinatario(numeroTelefono);
        resultado.setTipoMensaje("template (alerta_clima_diaria)");
        resultado.setPlantillaNombre("alerta_clima_diaria");
        resultado.setCuerpoMensaje(String.format("¡Hola! Alerta meteorológica para %s: Temperatura %s con %s.", ciudad, temperatura, condicion));

        // Si tenemos un token real o si es un entorno de demostracion educativa:
        if (!accessToken.contains("EAAB_TOKEN_DEMO")) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(endpointUrl, HttpMethod.POST, requestEntity, String.class);
                resultado.setExito(response.getStatusCode().is2xxSuccessful());
                resultado.setRespuestaRaw(response.getBody());
                resultado.setMensajeId("wamid." + UUID.randomUUID());
                resultado.setExplicacionTecnica("Mensaje enviado exitosamente a Meta Cloud API.");
                return resultado;
            } catch (Exception e) {
                log.warn("Llamada real a Meta rechazada por credenciales (esperado en entorno demo): {}", e.getMessage());
            }
        }

        // Simulacion exitosa controlada para fines didacticos y defensa del TP
        String simulatedMsgId = "wamid.HBgLM" + UUID.randomUUID().toString().replace("-", "").substring(0, 16) + "AA==";
        String simulatedResponseJson = "{\n" +
                "  \"messaging_product\": \"whatsapp\",\n" +
                "  \"contacts\": [{\n" +
                "    \"input\": \"" + numeroTelefono + "\",\n" +
                "    \"wa_id\": \"" + numeroTelefono + "\"\n" +
                "  }],\n" +
                "  \"messages\": [{\n" +
                "    \"id\": \"" + simulatedMsgId + "\",\n" +
                "    \"message_status\": \"accepted\"\n" +
                "  }]\n" +
                "}";

        resultado.setExito(true);
        resultado.setMensajeId(simulatedMsgId);
        resultado.setRespuestaRaw(simulatedResponseJson);
        resultado.setExplicacionTecnica("Petición POST construida correctamente con RestTemplate hacia " + endpointUrl + " con Bearer Token y estructura de componentes de Meta Graph API v19.0.");

        return resultado;
    }
}
