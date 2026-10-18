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
    @Autowired(required = false) private ingsoftware.gatinder.mapper.ZoneMapper zoneMapper;

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

    public ingsoftware.gatinder.dto.ZoneDto toDto(Zone zone) {
        if (zone == null) return null;
        if (zoneMapper != null) return zoneMapper.toDto(zone);
        return new ingsoftware.gatinder.dto.ZoneDto(zone.getId(), zone.getName(), zone.isDeleted());
    }

    public List<ingsoftware.gatinder.dto.ZoneDto> findAllDtos() throws ErrorService {
        List<Zone> zones = findAll();
        if (zoneMapper != null) return zoneMapper.toDtoList(zones);
        List<ingsoftware.gatinder.dto.ZoneDto> dtos = new java.util.ArrayList<>();
        for (Zone z : zones) dtos.add(toDto(z));
        return dtos;
    }

    public ingsoftware.gatinder.dto.ZoneDto findDtoById(String zoneId) throws ErrorService {
        return toDto(findById(zoneId));
    }

    @Transactional public ingsoftware.gatinder.dto.ZoneDto createZone(String name) throws ErrorService {
        validate(name);
        Zone zone = new Zone();
        zone.setId(UUID.randomUUID().toString());
        zone.setName(name);
        Zone saved = zoneRepository.save(zone);
        return toDto(saved);
    }

    @Transactional public ingsoftware.gatinder.dto.ZoneDto updateZone(String zoneId, String name) throws ErrorService {
        validate(name);
        Zone zone = findById(zoneId);
        zone.setName(name);
        Zone saved = zoneRepository.save(zone);
        return toDto(saved);
    }

    public void validate(String name) throws ErrorService {
        if (name == null || name.trim().isEmpty()) {
            throw new ErrorService("El nombre de la zona no puede ser nulo o vacío");
        }
    }
}
