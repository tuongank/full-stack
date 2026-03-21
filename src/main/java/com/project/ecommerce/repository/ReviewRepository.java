package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

}
