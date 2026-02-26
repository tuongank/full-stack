package com.project.ecommerce.controller;

import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.dto.Response;
import com.project.ecommerce.dto.UserDto;
import com.project.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest request) {
        Response response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid UserDto request) {
        Response response = authService.register(request);
        return ResponseEntity.ok(response);
    }

}
