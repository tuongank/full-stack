package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.CreateReviewRequest;
import com.project.ecommerce.dto.request.UpdateReviewRequest;
import com.project.ecommerce.dto.response.ReviewResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.Review;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.InteractionType;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final UserItemInteractionService userItemInteractionService;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        User currentUser = userService.getLoggedInUser();
        Product product = getProductByIdOrThrow(request.getProductId());

        validateCreateReview(currentUser.getId(), product.getId(), request.getRating());

        Review review = Review.builder()
                .content(normalizeContent(request.getContent()))
                .rating(request.getRating())
                .product(product)
                .user(currentUser)
                .build();

        Review savedReview = reviewRepository.save(review);

        // Log explicit feedback for recommender
        userItemInteractionService.logInteraction(currentUser, product, InteractionType.REVIEW);

        return toResponse(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        User currentUser = userService.getLoggedInUser();
        Review review = getReviewByIdOrThrow(reviewId);

        validateReviewPermission(review, currentUser);

        if (request.getContent() != null) {
            review.setContent(normalizeContent(request.getContent()));
        }

        if (request.getRating() != null) {
            validateRating(request.getRating());
            review.setRating(request.getRating());
        }

        Review updatedReview = reviewRepository.save(review);
        return toResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        User currentUser = userService.getLoggedInUser();
        Review review = getReviewByIdOrThrow(reviewId);

        validateReviewPermission(review, currentUser);
        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = getReviewByIdOrThrow(reviewId);
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProduct(Long productId, int page, int size) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found");
        }

        Page<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(
                productId,
                PageRequest.of(page, size)
        );

        return reviews.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(int page, int size) {
        User currentUser = userService.getLoggedInUser();

        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(
                currentUser.getId(),
                PageRequest.of(page, size)
        );

        return reviews.map(this::toResponse);
    }

    // =========================
    // Private helpers
    // =========================

    private Product getProductByIdOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private Review getReviewByIdOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    private void validateCreateReview(Long userId, Long productId, int rating) {
        validateRating(rating);

        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("You have already reviewed this product");
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private void validateReviewPermission(Review review, User currentUser) {
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You do not have permission to modify this review");
        }
    }

    private String normalizeContent(String content) {
        if (content == null) {
            return null;
        }

        String trimmed = content.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

