package ingsoftware.gatinder.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.ModelMap;
import jakarta.servlet.http.HttpSession;

import ingsoftware.gatinder.config.RememberMeInterceptor;
import ingsoftware.gatinder.dto.AuthenticatedUserDto;
import ingsoftware.gatinder.dto.LoginDto;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.service.ZoneService;
import ingsoftware.gatinder.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private ZoneService zoneService;

    @PostMapping("/register") public String register(ModelMap model, @ModelAttribute RegisterDto request) {
        try {
            userService.register(request);
        } catch (Exception e) {
            try {
                Collection<Zone> zones = zoneService.findAll();
                model.put("zones", zones);
            } catch (Exception ex) {
                model.put("error", "Error al obtener las zonas");
            }
            model.put("error", e.getMessage());
            model.put("firstName", request.getFirstName());
            model.put("lastName", request.getLastName());
            model.put("email", request.getEmail());
            model.put("password", request.getPassword());
            model.put("repeatPassword", request.getRepeatPassword());
            return "/register";
        }
        model.put("success", "Usuario registrado correctamente");
        return "/success";
    }

    @PostMapping("/login") public String login(ModelMap model, HttpSession session,
            @ModelAttribute LoginDto request, HttpServletResponse response) {
        try {
            AuthenticatedUserDto authenticatedUser = userService.authenticate(request);
            session.setAttribute("loggedUser", authenticatedUser.getUser());
            RememberMeInterceptor.addCookie(response, authenticatedUser.getRememberToken());
            return "redirect:/home";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            model.put("email", request.getEmail());
            model.put("password", request.getPassword());
            return "/login";
        }
    }

    @GetMapping("/logout") public String logout(HttpSession session,
            @CookieValue(value = RememberMeInterceptor.COOKIE_NAME, required = false) String token,
            HttpServletResponse response) {
        userService.clearRememberToken(token);
        session.invalidate();
        RememberMeInterceptor.deleteCookie(response);
        return "redirect:/login";
    }

    @GetMapping("/profile/edit") public String editProfile(HttpSession session, ModelMap model) {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            Collection<Zone> zones = zoneService.findAll();
            model.put("zones", zones);
            model.put("user", loggedUser);
            return "profile";
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las zonas", e);
        }
    }

    @GetMapping("/profile/update") public String updateProfile(HttpSession session, ModelMap model, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password, @RequestParam String repeatPassword, @RequestParam String zoneId) {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        try {
            userService.update(null, loggedUser.getId(), firstName, lastName, email, repeatPassword, password, zoneId);
            UserDto updatedUser = userService.findDtoById(loggedUser.getId());
            session.setAttribute("loggedUser", updatedUser);
            model.put("success", "Perfil actualizado correctamente");
            return "profile";
        } catch (Exception e) {
            try {
                Collection<Zone> zones = zoneService.findAll();
                model.put("zones", zones);
            } catch (Exception ex) {
                model.put("error", "Error al obtener las zonas");
            }
            model.put("error", e.getMessage());
            model.put("user", loggedUser);
            return "profile";
        }
    }
}
