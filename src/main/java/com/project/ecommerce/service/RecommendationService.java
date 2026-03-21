package com.project.ecommerce.service;

import com.project.ecommerce.dto.response.ProductResponse;
import com.project.ecommerce.dto.response.RecommendationResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.mapper.ProductMapper;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserService userService;

    // Thay url này nếu Python chạy port khác
    private static final String RECOMMENDER_API_URL = "http://localhost:8000/api/v1/recommendations/hybrid/";

    public List<ProductResponse> getHybridRecommendations(int limit) {
        try {
            User loggedInUser = userService.getLoggedInUser();
            Long userId = loggedInUser.getId();

            String url = UriComponentsBuilder.fromUriString(RECOMMENDER_API_URL + userId)
                    .queryParam("limit", limit)
                    .toUriString();

            RecommendationResponse response = restTemplate.getForObject(url, RecommendationResponse.class);

            if (response != null && response.getRecommended_product_ids() != null
                    && !response.getRecommended_product_ids().isEmpty()) {
                List<Long> productIds = response.getRecommended_product_ids();

                // Fetch products by id
                List<Product> products = productRepository.findAllById(productIds);

                // Map to response DTOs and return
                return products.stream()
                        .map(productMapper::toProductResponse)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            log.error("Failed to get recommendations from Python service: {}", e.getMessage());
        }

        // Return empty or fallback
        return Collections.emptyList();
    }
}
