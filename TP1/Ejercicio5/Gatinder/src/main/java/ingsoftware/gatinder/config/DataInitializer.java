package ingsoftware.gatinder.config;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.repository.PetRepository;
import ingsoftware.gatinder.repository.UserRepository;
import ingsoftware.gatinder.repository.ZoneRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicializar Zonas si no existen
        Zone defaultZone = null;
        if (zoneRepository.count() == 0) {
            String[] zoneNames = {"Centro", "Norte", "Sur", "Oeste", "Este"};
            for (String name : zoneNames) {
                Zone zone = new Zone();
                zone.setId(UUID.randomUUID().toString());
                zone.setName(name);
                zone.setDeleted(false);
                zoneRepository.save(zone);
                if (defaultZone == null) {
                    defaultZone = zone;
                }
            }
            System.out.println(">>> [DataInitializer] Zonas iniciales creadas con éxito.");
        } else {
            defaultZone = zoneRepository.findAll().get(0);
        }

        // 2. Inicializar Usuario de Prueba si no existe
        String testEmail = "usuario@gatinder.com";
        User testUser = userRepository.findByEmail(testEmail);
        if (testUser == null) {
            testUser = new User();
            testUser.setId(UUID.randomUUID().toString());
            testUser.setFirstName("Usuario");
            testUser.setLastName("Tester");
            testUser.setEmail(testEmail);
            testUser.setPassword("123456");
            testUser.setZone(defaultZone);
            testUser.setCreatedAt(Instant.now());
            testUser.setDeleted(false);
            userRepository.save(testUser);
            System.out.println(">>> [DataInitializer] Usuario de prueba creado: " + testEmail + " / 123456");
        }

        // 3. Inicializar Mascotas de Prueba (Activas y de Baja) para el usuario
        if (petRepository.count() == 0) {
            // Mascota Activa
            Pet petActive = new Pet();
            petActive.setId(UUID.randomUUID().toString());
            petActive.setName("Garfield");
            petActive.setGender(Gender.MALE);
            petActive.setAnimal(Animal.CAT);
            petActive.setUser(testUser);
            petActive.setCreatedAt(Instant.now());
            petActive.setDeleted(false);
            petRepository.save(petActive);

            // Mascota de Baja
            Pet petDeleted = new Pet();
            petDeleted.setId(UUID.randomUUID().toString());
            petDeleted.setName("Milo (Baja)");
            petDeleted.setGender(Gender.MALE);
            petDeleted.setAnimal(Animal.DOG);
            petDeleted.setUser(testUser);
            petDeleted.setCreatedAt(Instant.now());
            petDeleted.setDeleted(true);
            petDeleted.setDeletedAt(Instant.now());
            petRepository.save(petDeleted);

            System.out.println(">>> [DataInitializer] Mascotas de prueba (activas y de baja) creadas con éxito.");
        }
    }
}
