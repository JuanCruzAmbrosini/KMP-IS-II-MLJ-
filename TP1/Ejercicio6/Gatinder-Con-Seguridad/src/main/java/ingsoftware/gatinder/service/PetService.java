package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.enums.*;
import ingsoftware.gatinder.repository.PetRepository;
import ingsoftware.gatinder.repository.PetAuditRepository;
import ingsoftware.gatinder.entity.PetAudit;
import ingsoftware.gatinder.dto.PetDto;

@Service
public class PetService {
    @Autowired private PetRepository petRepository;
    @Autowired private UserService userService;
    @Autowired private PictureService pictureService;
    @Autowired private PetAuditRepository petAuditRepository;

    @Transactional public void create(MultipartFile file, String userId, String name, Gender gender, Animal animal) throws ErrorService {
        try {
            validate(name, gender, animal);
            User user = userService.findById(userId);
            Pet pet = new Pet();
            pet.setId(UUID.randomUUID().toString());
            pet.setName(name);
            pet.setGender(gender);
            pet.setAnimal(animal);
            pet.setUser(user);
            pet.setCreatedAt(Instant.now());
            if (file != null && !file.isEmpty()) {
                pet.setPicture(pictureService.create(file));
            }
            petRepository.save(pet);
            audit("CREATE", pet, userId, "Mascota creada");
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            String detail = e.getMessage() == null ? "revise los datos ingresados" : e.getMessage();
            throw new ErrorService("Error al agregar la mascota: " + detail);
        }
    }

    @Transactional public void update(MultipartFile file, String petId, String userId, String name, Gender gender, Animal animal) throws ErrorService {
        try {
            validate(name, gender, animal);
            Optional<Pet> response = petRepository.findById(petId);
            if (response.isPresent()) {
                Pet pet = response.get();
                if (pet.getUser().getId().equals(userId) && !pet.isDeleted()) {
                    pet.setName(name);
                    pet.setGender(gender);
                    pet.setAnimal(animal);
                    if (file != null && !file.isEmpty()) {
                        String pictureId = pet.getPicture() == null ? null : pet.getPicture().getId();
                        if (pictureId == null) {
                            pet.setPicture(pictureService.create(file));
                        } else {
                            pet.setPicture(pictureService.update(pictureId, file));
                        }
                    }
                    petRepository.save(pet);
                    audit("UPDATE", pet, userId, "Mascota modificada");
                } else {
                    throw new ErrorService("No tiene permiso para modificar esta mascota");
                }
            } else {
                throw new ErrorService("No se encontró la mascota con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al validar la mascota");
        }
    }

    @Transactional public void delete(String petId, String userId) throws ErrorService {
        try {
            Optional<Pet> response = petRepository.findById(petId);
            if (response.isPresent()) {
                Pet pet = response.get();
                if (pet.getUser().getId().equals(userId)) {
                    pet.setDeleted(true);
                    pet.setDeletedAt(Instant.now());
                    petRepository.save(pet);
                    audit("DELETE", pet, userId, "Mascota dada de baja");
                } else {
                    throw new ErrorService("No tiene permiso para eliminar esta mascota");
                }
            } else {
                throw new ErrorService("No se encontró la mascota con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al eliminar la mascota");
        }
    }

    public List<Pet> findAll() throws ErrorService {
        try {
            return petRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar las mascotas");
        }
    }

    public Pet findById(String petId) throws ErrorService {
        try {
            Optional<Pet> response = petRepository.findById(petId);
            if (response.isPresent()) {
                return response.get();
            } else {
                throw new ErrorService("No se encontró la mascota con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al obtener la mascota");
        }
    }

    public Pet findByIdForUser(String petId, String userId) throws ErrorService {
        Pet pet = findById(petId);
        if (pet.isDeleted() || pet.getUser() == null || !pet.getUser().getId().equals(userId)) {
            throw new ErrorService("No tiene permiso para consultar esta mascota");
        }
        return pet;
    }

    private void audit(String action, Pet pet, String userId, String details) {
        PetAudit audit = new PetAudit();
        audit.setAction(action);
        audit.setPetId(pet.getId());
        audit.setUserId(userId);
        audit.setDetails(details);
        audit.setOccurredAt(Instant.now());
        petAuditRepository.save(audit);
    }

    public List<Pet> findByUserId(String userId) throws ErrorService {
        try {
            return petRepository.findPetsByUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar las mascotas del usuario");
        }
    }

    public List<PetDto> findDtosByUserId(String userId) throws ErrorService {
        List<PetDto> pets = new java.util.ArrayList<>();
        for (Pet pet : findByUserId(userId)) {
            String pictureUrl = pet.getPicture() == null ? null : "/pictures/pet/" + pet.getId();
            pets.add(new PetDto(pet.getId(), pet.getName(), pet.getGender(), pet.getAnimal(),
                    pet.getUser().getId(), pictureUrl));
        }
        return pets;
    }

    public List<PetAudit> findAuditsByUserId(String userId) {
        return petAuditRepository.findByUserIdOrderByOccurredAtDesc(userId);
    }

    public void validate(String name, Gender gender) throws ErrorService {
        if (name == null || name.isEmpty()) {
            throw new ErrorService("El nombre de la mascota no puede ser nulo o vacío");
        }
        if (gender == null) {
            throw new ErrorService("El género de la mascota no puede ser nulo");
        }
    }

    public void validate(String name, Gender gender, Animal animal) throws ErrorService {
        validate(name, gender);
        if (animal == null) {
            throw new ErrorService("El tipo de animal no puede ser nulo");
        }
    }
}
