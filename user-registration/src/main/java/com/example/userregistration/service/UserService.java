package com.example.userregistration.service;

import com.example.userregistration.dto.UserRegistrationRequest;
import com.example.userregistration.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRegistrationRequest request);

    void deleteUser(Long id);
}
