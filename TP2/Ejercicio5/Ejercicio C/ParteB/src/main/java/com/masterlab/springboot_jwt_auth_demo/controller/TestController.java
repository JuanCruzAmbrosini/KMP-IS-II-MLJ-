package com.masterlab.springboot_jwt_auth_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Collections.singletonMap("message", "You accessed a protected endpoint!");
    }

    @PostMapping("/api/hello")
    public Map<String, String> helloPost() {
        return Collections.singletonMap("message", "You accessed a protected endpoint!");
    }
}

