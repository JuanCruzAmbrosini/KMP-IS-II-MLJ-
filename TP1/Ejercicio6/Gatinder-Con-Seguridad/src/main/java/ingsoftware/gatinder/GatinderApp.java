package ingsoftware.gatinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.repository.ZoneRepository;

@SpringBootApplication
public class GatinderApp {
    public static void main(String[] args) {
        SpringApplication.run(GatinderApp.class, args);
    }

    @Bean
    CommandLineRunner initializeZones(ZoneRepository zoneRepository) {
        return args -> {
            String[] zoneNames = {
                    "CABA", "Zona Norte", "Zona Oeste", "Zona Sur", "La Plata",
                    "Mar del Plata", "Rosario", "Cordoba", "Mendoza", "Tucuman"
            };

            for (String zoneName : zoneNames) {
                if (zoneRepository.findByName(zoneName) == null) {
                    Zone zone = new Zone();
                    zone.setId(java.util.UUID.randomUUID().toString());
                    zone.setName(zoneName);
                    zone.setDeleted(false);
                    zoneRepository.save(zone);
                }
            }
        };
    }
}
