package ingsoftware.gatinder.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ingsoftware.gatinder.dto.AuthenticatedUserDto;
import ingsoftware.gatinder.dto.LoginDto;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.UserService;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterDto request) throws ErrorService {
        UserDto registered = userService.register(request);
        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthenticatedUserDto> login(@RequestBody LoginDto request) throws ErrorService {
        AuthenticatedUserDto authenticated = userService.authenticate(request);
        return ResponseEntity.ok(authenticated);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> listUsers() throws ErrorService {
        List<UserDto> users = userService.findAllDtos();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) throws ErrorService {
        UserDto user = userService.findDtoById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String id,
            @RequestBody RegisterDto updateDto) throws ErrorService {
        userService.update(null, id, updateDto.getFirstName(), updateDto.getLastName(),
                updateDto.getEmail(), updateDto.getPassword(), updateDto.getRepeatPassword(),
                updateDto.getZoneId());
        UserDto updated = userService.findDtoById(id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable String id) throws ErrorService {
        userService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable String id) throws ErrorService {
        userService.activate(id);
        return ResponseEntity.ok().build();
    }
}
