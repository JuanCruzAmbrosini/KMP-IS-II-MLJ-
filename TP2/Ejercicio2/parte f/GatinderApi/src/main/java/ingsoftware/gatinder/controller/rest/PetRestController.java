package ingsoftware.gatinder.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;

@RestController
@RequestMapping("/api/pets")
public class PetRestController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<PetDto>> getAllPets() throws ErrorService {
        return ResponseEntity.ok(petService.findAllDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable String id) throws ErrorService {
        return ResponseEntity.ok(petService.findDtoById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PetDto>> getPetsByUserId(@PathVariable String userId) throws ErrorService {
        return ResponseEntity.ok(petService.findDtosByUserId(userId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto petDto) throws ErrorService {
        petService.create(null, petDto.getUserId(), petDto.getName(), petDto.getGender(), petDto.getAnimal());
        List<PetDto> userPets = petService.findDtosByUserId(petDto.getUserId());
        PetDto created = userPets.isEmpty() ? petDto : userPets.get(userPets.size() - 1);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetDto> createPetMultipart(
            @RequestParam("name") String name,
            @RequestParam("userId") String userId,
            @RequestParam("gender") ingsoftware.gatinder.enums.Gender gender,
            @RequestParam("animal") ingsoftware.gatinder.enums.Animal animal,
            @RequestPart(value = "file", required = false) MultipartFile file) throws ErrorService {
        petService.create(file, userId, name, gender, animal);
        List<PetDto> userPets = petService.findDtosByUserId(userId);
        PetDto created = userPets.isEmpty() ? null : userPets.get(userPets.size() - 1);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDto> updatePet(
            @PathVariable String id,
            @RequestBody PetDto petDto) throws ErrorService {
        petService.update(null, id, petDto.getUserId(), petDto.getName(), petDto.getGender(), petDto.getAnimal());
        return ResponseEntity.ok(petService.findDtoById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(
            @PathVariable String id,
            @RequestParam("userId") String userId) throws ErrorService {
        petService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
