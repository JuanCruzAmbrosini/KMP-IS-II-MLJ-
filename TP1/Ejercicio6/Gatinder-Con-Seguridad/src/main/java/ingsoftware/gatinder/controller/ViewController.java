package ingsoftware.gatinder.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.service.ZoneService;

@Controller
public class ViewController {
    @Autowired private ZoneService zoneService;

    @GetMapping("/") public String index() {
        return "index_1";
    }

    @GetMapping("/home") public String home(HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/login") public String login() {
        return "login";
    }

    @GetMapping("/register") public String register(ModelMap model) {
        try {
            Collection<Zone> zones = zoneService.findAll();
            model.put("zones", zones);
            return "register";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "";
        }
    }

}
