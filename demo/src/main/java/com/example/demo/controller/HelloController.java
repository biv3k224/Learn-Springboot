package com.example.demo.controller;

import com.example.demo.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

    @GetMapping("/status")
    public String getStatus() {
        return greetingService.getServiceStatus();
    }


    @GetMapping("/greet")
    public String greet() {
        return greetingService.getFormalGreeting();
    }

    @GetMapping
    public String casualGreet() {
        return greetingService.getCasualGreeting();
    }

    @GetMapping("/time")
    public String time() {
        return greetingService.greetTime();
    }
}

