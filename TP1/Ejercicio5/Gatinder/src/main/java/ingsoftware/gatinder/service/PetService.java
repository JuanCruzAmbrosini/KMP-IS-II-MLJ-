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
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.enums.*;
import ingsoftware.gatinder.repository.PetRepository;

@Service
public class PetService {
    @Autowired private PetRepository petRepository;
    @Autowired private UserService userService;
    @Autowired private PictureService pictureService;

    @Transactional public void create(MultipartFile file, String userId, String name, Gender gender, Animal animal) throws ErrorService {
        try {
            validate(name, gender);
            User user = userService.findById(userId);
            Pet pet = new Pet();
            pet.setId(UUID.randomUUID().toString());
            pet.setName(name);
            pet.setGender(gender);
            pet.setAnimal(animal);
            pet.setUser(user);
            pet.setCreatedAt(Instant.now());
            Picture picture = pictureService.create(file);
            pet.setPicture(picture);
            petRepository.save(pet);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al agregar la mascota");
        }
    }

    @Transactional public void update(MultipartFile file, String petId, String userId, String name, Gender gender, Animal animal) throws ErrorService {
        try {
            validate(name, gender);
            Optional<Pet> response = petRepository.findById(Long.valueOf(petId));
            if (response.isPresent()) {
                Pet pet = response.get();
                if (pet.getUser().getId().equals(userId)) {
                    pet.setName(name);
                    pet.setGender(gender);
                    pet.setAnimal(animal);
                    String pictureId = null;
                    if (pet.getPicture() != null) {
                        pictureId = pet.getPicture().getId().toString();
                    }
                    Picture picture = pictureService.update(pictureId, file);
                    pet.setPicture(picture);
                    petRepository.save(pet);
                }
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
            Optional<Pet> response = petRepository.findById(Long.valueOf(petId));
            if (response.isPresent()) {
                Pet pet = response.get();
                if (pet.getUser().getId().equals(userId)) {
                    pet.setDeleted(true);
                    petRepository.save(pet);
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
            Optional<Pet> response = petRepository.findById(Long.valueOf(petId));
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

    public List<Pet> findByUserId(String userId) throws ErrorService {
        try {
            return petRepository.findPetsByUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar las mascotas del usuario");
        }
    }

    public void validate(String name, Gender gender) throws ErrorService {
        if (name == null || name.isEmpty()) {
            throw new ErrorService("El nombre de la mascota no puede ser nulo o vacío");
        }
        if (gender == null) {
            throw new ErrorService("El género de la mascota no puede ser nulo");
        }
    }
}
