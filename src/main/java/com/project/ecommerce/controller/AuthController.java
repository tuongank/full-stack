package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.VerifyCodeRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.request.AuthRequest;
import com.project.ecommerce.dto.request.RegisterRequest;
import com.project.ecommerce.dto.response.AuthResponse;
import com.project.ecommerce.dto.response.RegisterResponse;
import com.project.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse authResponse = authService.login(request);
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status(200)
                .message("Login successful")
                .data(authResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse registerResponse = authService.register(request);
        ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
                .status(201)
                .message("Registration successful")
                .data(registerResponse)
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody @Valid VerifyCodeRequest request) {
        authService.verifyRegistrationCode(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Verification successful")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Void>> resendCode(@RequestParam String email) {
        authService.resendVerificationCode(email);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Verification code resent successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}
