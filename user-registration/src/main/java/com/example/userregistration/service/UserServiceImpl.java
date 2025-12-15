package com.example.userregistration.service;

import com.example.userregistration.dto.UserRegistrationRequest;
import com.example.userregistration.dto.UserResponse;
import com.example.userregistration.entity.User;
import com.example.userregistration.exception.DuplicateResourceException;
import com.example.userregistration.exception.ResourceNotFoundException;
import com.example.userregistration.repository.UserRepository;
import com.example.userregistration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Create new user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(), // In real app, you should hash this password!
                request.getFullName(),
                request.getPhoneNumber(),
                request.getAge()
        );

        // Save user
        User savedUser = userRepository.save(user);

        // Convert to response DTO
        return convertToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return convertToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRegistrationRequest request) {
        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Check if new username already exists (if changed)
        if (!existingUser.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check if new email already exists (if changed)
        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Update user fields
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(request.getPassword()); // Hash in real app!
        existingUser.setFullName(request.getFullName());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setAge(request.getAge());

        // Save updated user
        User updatedUser = userRepository.save(existingUser);
        return convertToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        // Check if user exists
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
    }

    // Helper method to convert Entity to Response DTO
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAge(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}