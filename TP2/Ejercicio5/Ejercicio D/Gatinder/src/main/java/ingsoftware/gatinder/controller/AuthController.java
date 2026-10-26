package ingsoftware.gatinder.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ingsoftware.gatinder.dto.AuthenticatedUserDto;
import ingsoftware.gatinder.dto.LoginDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private UserService userService;
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto request) throws ErrorService {
        AuthenticatedUserDto authenticatedUser = userService.authenticate(request);
        return ResponseEntity.ok(Map.of(
                "token", authenticatedUser.getRememberToken(),
                "tokenType", "Bearer",
                "expiresIn", "28800"));
    }
}
