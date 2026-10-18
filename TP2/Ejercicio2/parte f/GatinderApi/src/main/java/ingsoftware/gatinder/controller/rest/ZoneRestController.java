package ingsoftware.gatinder.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ingsoftware.gatinder.dto.ZoneDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.ZoneService;

@RestController
@RequestMapping("/api/zones")
public class ZoneRestController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping
    public ResponseEntity<List<ZoneDto>> getAllZones() throws ErrorService {
        return ResponseEntity.ok(zoneService.findAllDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable String id) throws ErrorService {
        return ResponseEntity.ok(zoneService.findDtoById(id));
    }

    @PostMapping
    public ResponseEntity<ZoneDto> createZone(@RequestBody ZoneDto zoneDto) throws ErrorService {
        ZoneDto created = zoneService.createZone(zoneDto.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDto> updateZone(
            @PathVariable String id,
            @RequestBody ZoneDto zoneDto) throws ErrorService {
        ZoneDto updated = zoneService.updateZone(id, zoneDto.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) throws ErrorService {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
