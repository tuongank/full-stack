package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.AuthRequest;
import com.project.ecommerce.dto.request.RegisterRequest;
import com.project.ecommerce.dto.response.AuthResponse;
import com.project.ecommerce.dto.response.RegisterResponse;
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

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("Email is incorrect"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new NotFoundException("Password is incorrect");
        }
        return AuthResponse.builder()
//                .token()
                .role(user.getRole().name())
                .build();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new NotFoundException("Email already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new NotFoundException("Phone already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
