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

import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.service.PetService;

@Controller
@RequestMapping("/pets")
public class PetController {
    @Autowired private PetService petService;

    @GetMapping("/pets/list") public String listPets(HttpSession session, ModelMap model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            List<Pet> pets = petService.findByUserId(loggedUser.getId());
            model.addAttribute("pets", pets);
            return "pet-list";
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de mascotas", e);
        }
    }

    @GetMapping("/pets/edit/{id}") public String editPet(HttpSession session, ModelMap model, @RequestParam(required = false) String id, @RequestParam(required = false) String action) {
        if (action == null) {
            action = "create";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
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

    @PostMapping("/pets/update") public String updatePet(HttpSession session, ModelMap model, MultipartFile file, @RequestParam(required = false) String id, @RequestParam String name, @RequestParam Gender gender, @RequestParam Animal animal, @RequestParam String action) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            if ("create".equals(action)) {
                petService.create(file, loggedUser.getId(), name, gender, animal);
            } else if ("update".equals(action)) {
                petService.update(file, id, loggedUser.getId(), name, gender, animal);
            }
            return "redirect:/pets/list";
        } catch (Exception e) {
            Pet pet = new Pet();
            pet.setId(id);
            pet.setName(name);
            pet.setGender(gender);
            pet.setAnimal(animal);
            model.put("action", action);
            model.put("pet", pet);
            model.put("genders", Gender.values());
            model.put("animals", Animal.values());
            model.put("error", e.getMessage());
            return "pet";
        }
    }

    @PostMapping("/pets/delete") public String deletePet(HttpSession session, @RequestParam String id) {
        User loggedUser = (User) session.getAttribute("loggedUser");
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
}