package com.project.ecommerce.controller;

import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.UserResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        UserResponse userResponse = userService.getUserByEmail(email);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(200)
                .message("User retrieved successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> userPage = userService.getAllUser(page, size);
        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .status(200)
                .message("Users retrieved successfully")
                .data(userPage.getContent())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getLoggedInUser() {
        User user = userService.getLoggedInUser();
        ApiResponse<User> response = ApiResponse.<User>builder()
                .status(200)
                .message("Logged in user retrieved successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

}
