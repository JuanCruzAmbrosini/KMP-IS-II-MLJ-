package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.repository.ZoneRepository;

@Service
public class ZoneService {
    @Autowired private ZoneRepository zoneRepository;

    @Transactional public void create(String name) throws ErrorService {
        try {
            validate(name);
            Zone zone = new Zone();
            zone.setId(UUID.randomUUID().toString());
            zone.setName(name);
            zoneRepository.save(zone);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al agregar la zona");
        }
    }

    @Transactional public void update(String zoneId, String name) throws ErrorService {
        try {
            validate(name);
            Optional<Zone> response = zoneRepository.findById(zoneId);
            if (response.isPresent()) {
                Zone zone = response.get();
                zone.setName(name);
                zoneRepository.save(zone);
            } else {
                throw new ErrorService("No se encontró la zona con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al actualizar la zona");
        }
    }

    @Transactional public void delete(String zoneId) throws ErrorService {
        try {
            Optional<Zone> response = zoneRepository.findById(zoneId);
            if (response.isPresent()) {
                Zone zone = response.get();
                zoneRepository.delete(zone);
            } else {
                throw new ErrorService("No se encontró la zona con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al eliminar la zona");
        }
    }

    public Zone findById(String zoneId) throws ErrorService {
        try {
            if (zoneId == null || zoneId.isEmpty()) {
                throw new ErrorService("El ID de la zona no puede ser nulo o vacío");
            }
            Optional<Zone> response = zoneRepository.findById(zoneId);
            if (response.isPresent()) {
                return response.get();
            } else {
                throw new ErrorService("No se encontró la zona con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al buscar la zona");
        }
    }

    public List<Zone> findAll() throws ErrorService {
        try {
            return zoneRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar las zonas");
        }
    }

    public void validate(String name) throws ErrorService {
        if (name == null || name.isEmpty()) {
            throw new ErrorService("El nombre de la zona no puede ser nulo o vacío");
        }
    }

}
