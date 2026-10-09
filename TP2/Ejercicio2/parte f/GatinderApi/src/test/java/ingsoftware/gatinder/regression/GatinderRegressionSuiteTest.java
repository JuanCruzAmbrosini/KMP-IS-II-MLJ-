package ingsoftware.gatinder.regression;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TIPO DE PRUEBA: PRUEBAS DE REGRESIÓN (REGRESSION TESTING)
 * Objetivo: Validar integralmente la suite de regresión troncal de Gatinder
 * para asegurar que ninguna modificación de código fracture los flujos críticos de la plataforma.
 */
@SpringBootTest
@Transactional
public class GatinderRegressionSuiteTest {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteService voteService;

    @Test
    @DisplayName("Regresion: Flujo troncal completo de Gatinder (Alta -> Interaccion -> Reportes)")
    void testRegresion_FlujoTroncal() throws Exception {
        // 1. Alta de Zona
        Zone zona = new Zone();
        zona.setId(UUID.randomUUID().toString());
        zona.setName("Belgrano");
        zona = zoneRepository.save(zona);
        assertNotNull(zona.getId());

        // 2. Alta de Usuarios
        User u1 = new User();
        u1.setId(UUID.randomUUID().toString());
        u1.setFirstName("Esteban");
        u1.setEmail("esteban@test.com");
        u1.setZone(zona);
        u1 = userRepository.save(u1);

        User u2 = new User();
        u2.setId(UUID.randomUUID().toString());
        u2.setFirstName("Lucia");
        u2.setEmail("lucia@test.com");
        u2.setZone(zona);
        u2 = userRepository.save(u2);

        // 3. Alta de Mascotas
        Pet p1 = new Pet();
        p1.setId(UUID.randomUUID().toString());
        p1.setName("Simba");
        p1.setGender(Gender.MALE);
        p1.setAnimal(Animal.CAT);
        p1.setUser(u1);
        p1.setCreatedAt(Instant.now());
        p1 = petRepository.save(p1);

        Pet p2 = new Pet();
        p2.setId(UUID.randomUUID().toString());
        p2.setName("Nala");
        p2.setGender(Gender.FEMALE);
        p2.setAnimal(Animal.CAT);
        p2.setUser(u2);
        p2.setCreatedAt(Instant.now());
        p2 = petRepository.save(p2);

        // 4. Votacion
        voteService.vote(u1.getId(), p1.getId(), p2.getId());
        var listaVotos = voteRepository.findAll();
        assertFalse(listaVotos.isEmpty());
        Vote voto = listaVotos.get(0);

        // 5. Respuesta (Match)
        voteService.respond(u2.getId(), voto.getId());
        Vote respondido = voteRepository.findById(voto.getId()).orElseThrow();
        assertNotNull(respondido.getResponseDate());

        // 6. Generacion de reporte de consolidacion
        var reporte = voteService.buildVoteReport();
        assertNotNull(reporte);
        assertTrue(reporte.stream().anyMatch(r -> r.getPetName().equals("Nala")));
    }
}
