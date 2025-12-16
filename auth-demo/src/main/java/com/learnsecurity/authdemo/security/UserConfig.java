package com.learnsecurity.authdemo.security;

import com.learnsecurity.authdemo.model.User;
import com.learnsecurity.authdemo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            // Create admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", encoder.encode("admin123"), "ADMIN");
                userRepository.save(admin);
                System.out.println("✓ Admin user created (admin/admin123)");
            }

            // Create normal users
            String[][] users = {
                    {"john", "password123", "USER"},
                    {"alice", "alice123", "USER"},
                    {"bob", "bob123", "USER"}
            };

            for (String[] userData : users) {
                if (!userRepository.existsByUsername(userData[0])) {
                    User user = new User(userData[0], encoder.encode(userData[1]), userData[2]);
                    userRepository.save(user);
                    System.out.println("✓ User created: " + userData[0] + "/" + userData[1]);
                }
            }
        };
    }
}