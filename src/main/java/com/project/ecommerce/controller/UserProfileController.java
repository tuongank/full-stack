package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.UserProfileRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.UserProfileResponse;
import com.project.ecommerce.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        UserProfileResponse data = userProfileService.getMyProfile();

        return ApiResponse.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User profile retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @PostMapping
    public ApiResponse<UserProfileResponse> createMyProfile(@RequestBody UserProfileRequest request) {
        UserProfileResponse data = userProfileService.createMyProfile(request);

        return ApiResponse.<UserProfileResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("User profile created successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody UserProfileRequest request) {
        UserProfileResponse data = userProfileService.updateMyProfile(request);

        return ApiResponse.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User profile updated successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }
}