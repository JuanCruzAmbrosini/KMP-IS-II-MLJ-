package ingsoftware.gatinder.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.ModelMap;
import jakarta.servlet.http.HttpSession;

import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.service.ZoneService;
import ingsoftware.gatinder.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private ZoneService zoneService;

    @PostMapping("/register") public String register(ModelMap model, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password, @RequestParam String repeatPassword, @RequestParam String zoneId) {
        try {
            userService.create(null, firstName, lastName, email, repeatPassword, password, zoneId);
        } catch (Exception e) {
            try {
                Collection<Zone> zones = zoneService.findAll();
                model.put("zones", zones);
            } catch (Exception ex) {
                model.put("error", "Error al obtener las zonas");
            }
            model.put("error", e.getMessage());
            model.put("firstName", firstName);
            model.put("lastName", lastName);
            model.put("email", email);
            model.put("password", password);
            model.put("repeatPassword", repeatPassword);
            return "/register";
        }
        model.put("success", "Usuario registrado correctamente");
        return "/success";
    }

    @PostMapping("/login") public String login(ModelMap model, HttpSession session, @RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.authenticate(email, password);
            session.setAttribute("loggedUser", user);
            return "redirect:/home";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            model.put("email", email);
            model.put("password", password);
            return "/login";
        }
    }

    @GetMapping("/logout") public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile/edit") public String editProfile(HttpSession session, ModelMap model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            Collection<Zone> zones = zoneService.findAll();
            model.put("zones", zones);
            model.put("user", loggedUser);
            return "profile-edit";
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las zonas", e);
        }
    }

    @GetMapping("/profile/update") public String updateProfile(HttpSession session, ModelMap model, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password, @RequestParam String repeatPassword, @RequestParam String zoneId) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            userService.update(null, loggedUser.getId(), firstName, lastName, email, repeatPassword, password, zoneId);
            User updatedUser = userService.findById(loggedUser.getId());
            session.setAttribute("loggedUser", updatedUser);
            model.put("success", "Perfil actualizado correctamente");
            return "profile-edit";
        } catch (Exception e) {
            try {
                Collection<Zone> zones = zoneService.findAll();
                model.put("zones", zones);
            } catch (Exception ex) {
                model.put("error", "Error al obtener las zonas");
            }
            model.put("error", e.getMessage());
            model.put("user", loggedUser);
            return "profile-edit";
        }
    }
}
