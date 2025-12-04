package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GreetingService {

    @Value("${app.welcome.message}")
    private String welcomeMessage;
    public String getFormalGreeting(){
        return "Hello Bivek, Welcome to our service";
    }

    public String getCasualGreeting(){
        return "hey Bivek how is it going?";
    }

    public String getServiceStatus(){
        return "All system are working fine";
    }

    public String greetTime(){
        return "hello current time is " +  LocalDateTime.now();
    }
}
