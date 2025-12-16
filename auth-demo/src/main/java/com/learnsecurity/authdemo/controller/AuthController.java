package com.learnsecurity.authdemo.controller;

import com.learnsecurity.authdemo.model.User;
import com.learnsecurity.authdemo.repository.UserRepository;
import com.learnsecurity.authdemo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Request DTO for login
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Response DTO for login
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private String username;
        private String role;

        public LoginResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    // Request DTO for registration
    public static class RegisterRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Response DTO for registration
    public static class RegisterResponse {
        private String message;
        private String username;

        public RegisterResponse(String message, String username) {
            this.message = message;
            this.username = username;
        }

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // Response DTO for errors
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters and Setters
        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Get user role
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
                    })
                    .orElse("USER");

            return ResponseEntity.ok(new LoginResponse(token, loginRequest.getUsername(), role));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Authentication Failed", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", "Username is required"));
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", "Password must be at least 6 characters"));
        }

        // Check if user exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration Failed", "Username already exists"));
        }

        try {
            // Create new user
            User user = new User(
                    registerRequest.getUsername(),
                    passwordEncoder.encode(registerRequest.getPassword()),
                    "USER"  // Default role
            );

            userRepository.save(user);

            return ResponseEntity.ok(
                    new RegisterResponse("User registered successfully", registerRequest.getUsername())
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Registration Failed", "An error occurred during registration"));
        }
    }

    // Optional: Get current user info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Unauthorized", "Not authenticated"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
                })
                .orElse("USER");

        return ResponseEntity.ok(new LoginResponse(null, userDetails.getUsername(), role));
    }

    // Optional: Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
}