package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.ProductFeatureRequest;
import com.project.ecommerce.dto.response.ProductFeatureResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductFeature;
import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.ProductFeatureRepository;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductFeatureService {

    private final ProductRepository productRepository;
    private final ProductFeatureRepository productFeatureRepository;

    @Transactional
    public ProductFeatureResponse createFeature(Long productId, ProductFeatureRequest request) {
        Product product = getProductByIdOrThrow(productId);

        if (productFeatureRepository.existsByProductId(productId)) {
            throw new IllegalStateException("Product feature already exists for product id: " + productId);
        }

        ProductFeature productFeature = ProductFeature.builder()
                .product(product)
                .skinType(request.getSkinType())
                .skinConcerns(toSafeSkinConcernSet(request.getSkinConcerns()))
                .ingredients(toSafeStringSet(request.getIngredients()))
                .freeFrom(toSafeFreeFromSet(request.getFreeFrom()))
                .texture(request.getTexture())
                .usageTime(request.getUsageTime())
                .phLevel(request.getPhLevel())
                .pregnancySafe(request.getPregnancySafe())
                .build();

        ProductFeature savedFeature = productFeatureRepository.save(productFeature);
        return toResponse(savedFeature);
    }

    @Transactional
    public ProductFeatureResponse updateFeature(Long productId, ProductFeatureRequest request) {
        ProductFeature productFeature = productFeatureRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product feature not found for product id: " + productId));

        if (request.getSkinType() != null) {
            productFeature.setSkinType(request.getSkinType());
        }

        if (request.getSkinConcerns() != null) {
            productFeature.setSkinConcerns(toSafeSkinConcernSet(request.getSkinConcerns()));
        }

        if (request.getIngredients() != null) {
            productFeature.setIngredients(toSafeStringSet(request.getIngredients()));
        }

        if (request.getFreeFrom() != null) {
            productFeature.setFreeFrom(toSafeFreeFromSet(request.getFreeFrom()));
        }

        if (request.getTexture() != null) {
            productFeature.setTexture(request.getTexture());
        }

        if (request.getUsageTime() != null) {
            productFeature.setUsageTime(request.getUsageTime());
        }

        if (request.getPhLevel() != null) {
            productFeature.setPhLevel(request.getPhLevel());
        }

        if (request.getPregnancySafe() != null) {
            productFeature.setPregnancySafe(request.getPregnancySafe());
        }

        ProductFeature updatedFeature = productFeatureRepository.save(productFeature);
        return toResponse(updatedFeature);
    }

    @Transactional(readOnly = true)
    public ProductFeatureResponse getFeatureByProductId(Long productId) {
        ProductFeature productFeature = productFeatureRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product feature not found for product id: " + productId));

        return toResponse(productFeature);
    }

    @Transactional
    public void deleteFeatureByProductId(Long productId) {
        if (!productFeatureRepository.existsByProductId(productId)) {
            throw new NotFoundException("Product feature not found for product id: " + productId);
        }
        productFeatureRepository.deleteByProductId(productId);
    }

    private Product getProductByIdOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private ProductFeatureResponse toResponse(ProductFeature feature) {
        return ProductFeatureResponse.builder()
                .id(feature.getId())
                .productId(feature.getProduct().getId())
                .productName(feature.getProduct().getName())
                .skinType(feature.getSkinType())
                .skinConcerns(toSafeSkinConcernSet(feature.getSkinConcerns()))
                .ingredients(toSafeStringSet(feature.getIngredients()))
                .freeFrom(toSafeFreeFromSet(feature.getFreeFrom()))
                .texture(feature.getTexture())
                .usageTime(feature.getUsageTime())
                .phLevel(feature.getPhLevel())
                .pregnancySafe(feature.getPregnancySafe())
                .build();
    }

    private Set<SkinConcern> toSafeSkinConcernSet(Set<SkinConcern> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }

    private Set<String> toSafeStringSet(Set<String> source) {
        if (source == null) {
            return new HashSet<>();
        }

        Set<String> normalized = new HashSet<>();
        for (String item : source) {
            if (item != null && !item.isBlank()) {
                normalized.add(item.trim());
            }
        }
        return normalized;
    }

    private Set<FreeFrom> toSafeFreeFromSet(Set<FreeFrom> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }
}
