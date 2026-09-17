package ingsoftware.gatinder.acceptance;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Vote;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.repository.PetRepository;
import ingsoftware.gatinder.repository.UserRepository;
import ingsoftware.gatinder.repository.VoteRepository;
import ingsoftware.gatinder.repository.ZoneRepository;
import ingsoftware.gatinder.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBAS DE ACEPTACIÓN (UAT / BDD)
 * Objetivo: Validar criterios de aceptación de negocio estructurados en formato
 * Dado-Cuando-Entonces (Given-When-Then) para los flujos principales de la aplicación.
 *
 * HISTORIA DE USUARIO:
 * "Como dueño de una mascota registrada en Gatinder, quiero poder dar 'like' a otra mascota
 * compatible de mi zona y que el otro dueño pueda responder favorablemente para concretar un Match."
 */
@SpringBootTest
@Transactional
public class MatchAcceptanceTest {

    @Autowired
    private VoteService voteService;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VoteRepository voteRepository;

    private User dueno1;
    private User dueno2;
    private Pet mascota1;
    private Pet mascota2;

    @BeforeEach
    void setupEscenario() {
        Zone zona = new Zone();
        zona.setId(UUID.randomUUID().toString());
        zona.setName("Recoleta");
        zona = zoneRepository.save(zona);

        dueno1 = new User();
        dueno1.setId(UUID.randomUUID().toString());
        dueno1.setFirstName("Martin");
        dueno1.setEmail("martin@test.com");
        dueno1.setZone(zona);
        dueno1 = userRepository.save(dueno1);

        dueno2 = new User();
        dueno2.setId(UUID.randomUUID().toString());
        dueno2.setFirstName("Camila");
        dueno2.setEmail("camila@test.com");
        dueno2.setZone(zona);
        dueno2 = userRepository.save(dueno2);

        mascota1 = new Pet();
        mascota1.setId(UUID.randomUUID().toString());
        mascota1.setName("Felix");
        mascota1.setGender(Gender.MALE);
        mascota1.setAnimal(Animal.CAT);
        mascota1.setUser(dueno1);
        mascota1.setCreatedAt(Instant.now());
        mascota1 = petRepository.save(mascota1);

        mascota2 = new Pet();
        mascota2.setId(UUID.randomUUID().toString());
        mascota2.setName("Mimi");
        mascota2.setGender(Gender.FEMALE);
        mascota2.setAnimal(Animal.CAT);
        mascota2.setUser(dueno2);
        mascota2.setCreatedAt(Instant.now());
        mascota2 = petRepository.save(mascota2);
    }

    @Test
    @DisplayName("Criterio de Aceptacion 1: Emision de like genera un voto pendiente")
    void escenario_EmisionDeLike() throws Exception {
        // GIVEN (Dado): Martin tiene a su mascota 'Felix' y Camila a 'Mimi'
        assertNotNull(mascota1.getId());
        assertNotNull(mascota2.getId());

        // WHEN (Cuando): Martin vota por 'Mimi'
        voteService.vote(dueno1.getId(), mascota1.getId(), mascota2.getId());

        // THEN (Entonces): Se genera un voto con fecha de envio y sin fecha de respuesta todavia
        var votos = voteRepository.findAll();
        Vote votoGenerado = votos.stream()
                .filter(v -> v.getSenderPet().getId().equals(mascota1.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(votoGenerado, "Debe existir el voto registrado");
        assertNotNull(votoGenerado.getDate(), "Debe registrarse la fecha del like");
        assertNull(votoGenerado.getResponseDate(), "La respuesta debe estar inicialmente vacia");
    }

    @Test
    @DisplayName("Criterio de Aceptacion 2: Respuesta al voto consolida el Match")
    void escenario_RespuestaAlVotoConsolidaMatch() throws Exception {
        // GIVEN (Dado): Un voto emitido previamente de Felix hacia Mimi
        voteService.vote(dueno1.getId(), mascota1.getId(), mascota2.getId());
        Vote votoPendiente = voteRepository.findAll().get(0);

        // WHEN (Cuando): Camila responde al voto recibido por Mimi
        voteService.respond(dueno2.getId(), votoPendiente.getId());

        // THEN (Entonces): El voto cuenta con fecha de respuesta completando el match
        Vote votoActualizado = voteRepository.findById(votoPendiente.getId()).orElseThrow();
        assertNotNull(votoActualizado.getResponseDate(), "El match queda sellado con la fecha de respuesta");
    }
}
