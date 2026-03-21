package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.CreateReviewRequest;
import com.project.ecommerce.dto.request.UpdateReviewRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.ReviewResponse;
import com.project.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse data = reviewService.createReview(request);

        return ApiResponse.<ReviewResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Review created successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        ReviewResponse data = reviewService.updateReview(reviewId, request);

        return ApiResponse.<ReviewResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Review updated successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Review deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse data = reviewService.getReviewById(reviewId);

        return ApiResponse.<ReviewResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Review retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> reviewPage = reviewService.getReviewsByProduct(productId, page, size);

        return ApiResponse.<List<ReviewResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product reviews retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<ReviewResponse>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> reviewPage = reviewService.getMyReviews(page, size);

        return ApiResponse.<List<ReviewResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("My reviews retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(reviewPage.getContent())
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .build();
    }
}
