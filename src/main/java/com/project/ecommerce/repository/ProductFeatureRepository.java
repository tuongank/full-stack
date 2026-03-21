package com.project.ecommerce.repository;

import com.project.ecommerce.entity.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long> {
    Optional<ProductFeature> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    void deleteByProductId(Long productId);
}
