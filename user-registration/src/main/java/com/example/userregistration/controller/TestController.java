package com.example.userregistration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Serve HTML dashboard
    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "User Registration API Dashboard");
        return "index";
    }

    // Keep API endpoints as JSON
    @RestController
    @RequestMapping("/api/test")
    class TestApiController {

        @GetMapping("/db")
        public String testDatabase() {
            try {
                String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
                return "✅ Database Connected!\nPostgreSQL Version: " + version;
            } catch (Exception e) {
                return "❌ Database Connection Failed!\nError: " + e.getMessage();
            }
        }

        @GetMapping("/ping")
        public String ping() {
            return "🚀 User Registration API is running!";
        }
    }
}