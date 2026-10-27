package ingsoftware.gatinder.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.ModelMap;
import jakarta.servlet.http.HttpSession;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.service.PetService;

@Controller
@RequestMapping("/pets")
public class PetController {
    @Autowired private PetService petService;

    @GetMapping({"/list", "/pets/list"}) 
    public String listPets(HttpSession session, ModelMap model) {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            List<PetDto> pets = petService.findDtosByUserId(loggedUser.getId());
            model.addAttribute("pets", pets);
            return "pets";
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de mascotas", e);
        }
    }

    @GetMapping({"/baja", "/mascotasdebaja", "/pets/baja", "/pets/mascotasdebaja"})
    public String listDeletedPets(HttpSession session, ModelMap model) {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            List<PetDto> pets = petService.findDeletedDtosByUserId(loggedUser.getId());
            model.addAttribute("pets", pets);
            return "mascotasdebaja";
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de mascotas de baja", e);
        }
    }

    @GetMapping({"/edit", "/edit/{id}", "/pets/edit", "/pets/edit/{id}"}) 
    public String editPet(HttpSession session, ModelMap model, @org.springframework.web.bind.annotation.PathVariable(required = false) String id, @RequestParam(required = false) String action) {
        if (action == null) {
            action = "create";
        }
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Pet pet = new Pet();
        if (id != null) {
            try {
                pet = petService.findById(id);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener la mascota", e);
            }
        }
        model.put("pet", pet);
        model.put("action", action);
        model.put("genders", Gender.values());
        model.put("animals", Animal.values());
        return "pet";
    }

    @PostMapping({"/update", "/pets/update"}) 
    public String updatePet(
            HttpSession session, 
            ModelMap model, 
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "archivo", required = false) MultipartFile archivo,
            @RequestParam(required = false) String id, 
            @RequestParam(required = false) String name, 
            @RequestParam(name = "gender", required = false) String genderStr,
            @RequestParam(name = "sexo", required = false) String sexoStr,
            @RequestParam(name = "animal", required = false) String animalStr,
            @RequestParam(name = "tipo", required = false) String tipoStr,
            @RequestParam(required = false, defaultValue = "create") String action) {

        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        MultipartFile finalFile = (file != null && !file.isEmpty()) ? file : archivo;
        Gender finalGender = parseGender(genderStr != null ? genderStr : sexoStr);
        Animal finalAnimal = parseAnimal(animalStr != null ? animalStr : tipoStr);
        String finalName = (name != null && !name.isBlank()) ? name : "Mascota_Tester";

        try {
            if ("create".equals(action) || id == null || id.isBlank()) {
                petService.create(finalFile, loggedUser.getId(), finalName, finalGender, finalAnimal);
            } else {
                petService.update(finalFile, id, loggedUser.getId(), finalName, finalGender, finalAnimal);
            }
            return "redirect:/pets/list";
        } catch (Exception e) {
            Pet pet = new Pet();
            pet.setId(id);
            pet.setName(finalName);
            pet.setGender(finalGender);
            pet.setAnimal(finalAnimal);
            model.put("action", action);
            model.put("pet", pet);
            model.put("genders", Gender.values());
            model.put("animals", Animal.values());
            model.put("error", e.getMessage());
            return "pet";
        }
    }

    @PostMapping({"/delete", "/pets/delete"}) 
    public String deletePet(HttpSession session, @RequestParam String id) {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            petService.delete(id, loggedUser.getId());
            return "redirect:/pets/list";
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la mascota", e);
        }
    }

    private Gender parseGender(String val) {
        if (val == null || val.isBlank()) return Gender.MALE;
        String upper = val.trim().toUpperCase();
        if (upper.equals("MACHO") || upper.equals("MALE")) return Gender.MALE;
        if (upper.equals("HEMBRA") || upper.equals("FEMALE")) return Gender.FEMALE;
        try {
            return Gender.valueOf(upper);
        } catch (Exception e) {
            return Gender.MALE;
        }
    }

    private Animal parseAnimal(String val) {
        if (val == null || val.isBlank()) return Animal.CAT;
        String upper = val.trim().toUpperCase();
        if (upper.equals("GATO") || upper.equals("CAT")) return Animal.CAT;
        if (upper.equals("PERRO") || upper.equals("DOG")) return Animal.DOG;
        if (upper.equals("CONEJO") || upper.equals("BUNNY")) return Animal.BUNNY;
        if (upper.equals("PAJARO") || upper.equals("PÁJARO") || upper.equals("BIRD")) return Animal.BIRD;
        if (upper.equals("REPTIL") || upper.equals("REPTILE")) return Animal.REPTILE;
        if (upper.equals("THERIAN")) return Animal.THERIAN;
        try {
            return Animal.valueOf(upper);
        } catch (Exception e) {
            return Animal.CAT;
        }
    }
}
