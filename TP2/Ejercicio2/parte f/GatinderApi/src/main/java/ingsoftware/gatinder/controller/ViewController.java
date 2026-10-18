package ingsoftware.gatinder.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.service.ZoneService;

@Controller
public class ViewController {
    @Autowired private ZoneService zoneService;

    @GetMapping("/") public String index() {
        return "index";
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

    @GetMapping("/home") public String home(Model model) {
        return "home";
    }

}
