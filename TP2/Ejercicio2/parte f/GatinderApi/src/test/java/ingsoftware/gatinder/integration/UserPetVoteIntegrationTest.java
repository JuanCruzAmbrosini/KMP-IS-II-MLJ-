package ingsoftware.gatinder.integration;

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
 * TIPO DE PRUEBA: PRUEBA DE INTEGRACIÓN (INTEGRATION TESTING)
 * Objetivo: Validar el flujo integrado de persistencia y comunicación entre
 * repositorios y servicios Spring Data JPA con base de datos real (SQLite in-memory),
 * comprobando relaciones entre Zone, User, Pet y Vote.
 */
@SpringBootTest
@Transactional
public class UserPetVoteIntegrationTest {

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
    @DisplayName("Integration Test: Flujo completo de persistencia (Zona -> Usuario -> Mascota -> Voto -> Reporte)")
    void testFlujoCompletoPersistenciaIntegrada() throws Exception {
        // 1. Guardar Zona
        Zone zona = new Zone();
        zona.setId(UUID.randomUUID().toString());
        zona.setName("Palermo");
        zona = zoneRepository.save(zona);
        assertNotNull(zona.getId());

        // 2. Guardar Usuarios
        User user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setFirstName("Lucas");
        user1.setLastName("Gomez");
        user1.setEmail("lucas@test.com");
        user1.setPassword("123456");
        user1.setZone(zona);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setFirstName("Sofia");
        user2.setLastName("Lopez");
        user2.setEmail("sofia@test.com");
        user2.setPassword("654321");
        user2.setZone(zona);
        user2 = userRepository.save(user2);

        // 3. Guardar Mascotas
        Pet pet1 = new Pet();
        pet1.setId(UUID.randomUUID().toString());
        pet1.setName("Garfield");
        pet1.setGender(Gender.MALE);
        pet1.setAnimal(Animal.CAT);
        pet1.setUser(user1);
        pet1.setCreatedAt(Instant.now());
        pet1 = petRepository.save(pet1);

        Pet pet2 = new Pet();
        pet2.setId(UUID.randomUUID().toString());
        pet2.setName("Arlene");
        pet2.setGender(Gender.FEMALE);
        pet2.setAnimal(Animal.CAT);
        pet2.setUser(user2);
        pet2.setCreatedAt(Instant.now());
        pet2 = petRepository.save(pet2);

        // 4. Ejecutar voto mediante el servicio integrado
        voteService.vote(user1.getId(), pet1.getId(), pet2.getId());

        // 5. Verificar persistencia del voto en base de datos
        var votos = voteRepository.findAll();
        assertFalse(votos.isEmpty(), "El voto debe haber sido guardado en la base de datos");
        Vote votoGuardado = votos.get(0);
        assertEquals(pet1.getId(), votoGuardado.getSenderPet().getId());
        assertEquals(pet2.getId(), votoGuardado.getReceiverPet().getId());

        // 6. Validar reporte consolidado de votos
        var reporte = voteService.buildVoteReport();
        assertNotNull(reporte);
        assertTrue(reporte.stream().anyMatch(r -> r.getPetName().equals("Arlene")));
    }
}
