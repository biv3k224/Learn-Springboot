package com.learnsecurity.authdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // Returns index.html from templates folder
    }

    // Optional: Add more pages if needed
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}