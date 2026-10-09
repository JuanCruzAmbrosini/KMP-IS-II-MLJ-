package ingsoftware.gatinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ingsoftware.gatinder.client.GatinderRestClient;
import ingsoftware.gatinder.dto.ExternalFactDto;

@RestController
@RequestMapping("/api/client")
public class GatinderClientRestController {

    @Autowired
    private GatinderRestClient restClient;

    @GetMapping("/external/fact")
    public ResponseEntity<ExternalFactDto> getExternalFact() {
        ExternalFactDto fact = restClient.getRandomPetFact();
        return ResponseEntity.ok(fact);
    }

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("Gatinder REST API & RestTemplate Client operational");
    }
}
