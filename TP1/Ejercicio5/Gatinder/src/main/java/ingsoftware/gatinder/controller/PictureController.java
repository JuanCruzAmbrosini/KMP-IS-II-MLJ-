package ingsoftware.gatinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ingsoftware.gatinder.service.UserService;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;



@Controller
@RequestMapping("/pictures")
public class PictureController {
    @Autowired private UserService userService;
    @Autowired private PetService petService;

    @GetMapping("/user/{id}") public ResponseEntity<byte[]> userPicture(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            if (user.getPicture() == null) {
                throw new ErrorService("El usuario no posee foto asignada");
            }
            byte[] picture = user.getPicture().getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(picture, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pet/{id}") public ResponseEntity<byte[]> petPicture(@PathVariable String id) {
        try {
            Pet pet = petService.findById(id);
            if (pet.getPicture() == null) {
                throw new ErrorService("La mascota no posee foto asignada");
            }
            byte[] picture = pet.getPicture().getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(picture, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
