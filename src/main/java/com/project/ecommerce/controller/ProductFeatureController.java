package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.ProductFeatureRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.ProductFeatureResponse;
import com.project.ecommerce.service.ProductFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductFeatureController {

    private final ProductFeatureService productFeatureService;

    @PostMapping("/{productId}/feature")
    public ApiResponse<ProductFeatureResponse> createFeature(
            @PathVariable Long productId,
            @RequestBody ProductFeatureRequest request
    ) {
        ProductFeatureResponse data = productFeatureService.createFeature(productId, request);

        return ApiResponse.<ProductFeatureResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Product feature created successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @PutMapping("/{productId}/feature")
    public ApiResponse<ProductFeatureResponse> updateFeature(
            @PathVariable Long productId,
            @RequestBody ProductFeatureRequest request
    ) {
        ProductFeatureResponse data = productFeatureService.updateFeature(productId, request);

        return ApiResponse.<ProductFeatureResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product feature updated successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @GetMapping("/{productId}/feature")
    public ApiResponse<ProductFeatureResponse> getFeature(@PathVariable Long productId) {
        ProductFeatureResponse data = productFeatureService.getFeatureByProductId(productId);

        return ApiResponse.<ProductFeatureResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product feature retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @DeleteMapping("/{productId}/feature")
    public ApiResponse<Void> deleteFeature(@PathVariable Long productId) {
        productFeatureService.deleteFeatureByProductId(productId);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product feature deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

