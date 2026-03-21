package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.InteractionRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.InteractionResponse;
import com.project.ecommerce.service.UserItemInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class UserItemInteractionController {

    private final UserItemInteractionService userItemInteractionService;

    @PostMapping
    public ApiResponse<InteractionResponse> createInteraction(@RequestBody InteractionRequest request) {
        InteractionResponse data = userItemInteractionService.createMyInteraction(request);

        return ApiResponse.<InteractionResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Interaction created successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<Page<InteractionResponse>> getMyInteractions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InteractionResponse> data = userItemInteractionService.getMyInteractions(page, size);

        return ApiResponse.<Page<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .totalPages(data.getTotalPages())
                .totalElements(data.getTotalElements())
                .build();
    }
}
