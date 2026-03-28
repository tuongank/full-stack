package com.project.ecommerce.controller;

import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.WishlistResponse;
import com.project.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<Page<WishlistResponse>> getMyWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<WishlistResponse> data = wishlistService.getMyWishlist(page, size);
        return ApiResponse.<Page<WishlistResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @PostMapping("/{productId}")
    public ApiResponse<WishlistResponse> addToWishlist(@PathVariable Long productId) {
        WishlistResponse data = wishlistService.addToWishlist(productId);
        return ApiResponse.<WishlistResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Product added to wishlist")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product removed from wishlist")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/check/{productId}")
    public ApiResponse<Map<String, Boolean>> checkInWishlist(@PathVariable Long productId) {
        boolean inWishlist = wishlistService.isInWishlist(productId);
        return ApiResponse.<Map<String, Boolean>>builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist check completed")
                .timestamp(LocalDateTime.now())
                .data(Map.of("inWishlist", inWishlist))
                .build();
    }

    @GetMapping("/count")
    public ApiResponse<Map<String, Long>> getWishlistCount() {
        long count = wishlistService.getWishlistCount();
        return ApiResponse.<Map<String, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist count retrieved")
                .timestamp(LocalDateTime.now())
                .data(Map.of("count", count))
                .build();
    }
}
