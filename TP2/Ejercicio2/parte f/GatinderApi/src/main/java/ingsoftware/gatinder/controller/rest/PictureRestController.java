package ingsoftware.gatinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;
import ingsoftware.gatinder.service.PictureService;
import ingsoftware.gatinder.service.UserService;

@RestController
@RequestMapping("/api/pictures")
public class PictureRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private PictureService pictureService;

    @GetMapping("/user/{id}")
    public ResponseEntity<byte[]> getUserPicture(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            if (user.getPicture() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] data = user.getPicture().getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pet/{id}")
    public ResponseEntity<byte[]> getPetPicture(@PathVariable String id) {
        try {
            Pet pet = petService.findById(id);
            if (pet.getPicture() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] data = pet.getPicture().getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPicture(@RequestParam("file") MultipartFile file) throws ErrorService {
        Picture picture = pictureService.create(file);
        return new ResponseEntity<>(picture.getId(), HttpStatus.CREATED);
    }
}
