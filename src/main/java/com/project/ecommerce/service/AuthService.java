package com.project.ecommerce.service;

import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.dto.Response;
import com.project.ecommerce.dto.UserDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public Response login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("Email is incorrect"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new NotFoundException("Password is incorrect");
        }
        return Response.builder()
                .status(200)
                .message("Login successful")
                .build();
    }

    public Response register(UserDto request) {
        UserRole role = UserRole.USER;
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new NotFoundException("Email already exists");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .role(role)
                .phoneNumber(request.getPhoneNumber())
                .build();

        userRepository.save(user);
        return Response.builder()
                .status(200)
//                .user(U)
                .message("Registration successful")
                .build();
    }
}
