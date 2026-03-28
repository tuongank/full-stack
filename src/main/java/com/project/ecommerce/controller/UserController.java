package com.project.ecommerce.controller;

import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.UserDetailResponse;
import com.project.ecommerce.dto.response.UserResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserProfile;
import com.project.ecommerce.service.UserService;
import com.project.ecommerce.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;

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
    public ResponseEntity<ApiResponse<UserDetailResponse>> getLoggedInUser() {
        User user = userService.getLoggedInUser();
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        
        UserDetailResponse data = UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .skinType(profile != null ? profile.getSkinType() : null)
                .skinConcerns(profile != null ? profile.getSkinConcerns() : null)
                .avoidIngredients(profile != null ? profile.getAvoidIngredients() : null)
                .age(profile != null ? profile.getAge() : null)
                .createdAt(user.getCreatedDate())
                .updatedAt(user.getUpdatedDate())
                .build();
                
        ApiResponse<UserDetailResponse> response = ApiResponse.<UserDetailResponse>builder()
                .status(200)
                .message("Logged in user retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

}
