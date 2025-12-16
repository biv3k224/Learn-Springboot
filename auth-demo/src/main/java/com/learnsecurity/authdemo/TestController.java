package com.learnsecurity.authdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Public endpoint - No login required!";
    }

    @GetMapping("/home")
    public String home() {
        return "Welcome to Home Page! (Protected endpoint)";
    }
}