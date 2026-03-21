package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.InteractionRequest;
import com.project.ecommerce.dto.response.InteractionResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserItemInteraction;
import com.project.ecommerce.enums.InteractionType;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.UserItemInteractionRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserItemInteractionService {

    private final UserItemInteractionRepository userItemInteractionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public InteractionResponse createMyInteraction(InteractionRequest request) {
        User currentUser = userService.getLoggedInUser();
        Product product = getProductByIdOrThrow(request.getProductId());

        if (request.getType() == null) {
            throw new IllegalArgumentException("Interaction type is required");
        }

        UserItemInteraction interaction = UserItemInteraction.builder()
                .user(currentUser)
                .product(product)
                .type(request.getType())
                .build();

        UserItemInteraction savedInteraction = userItemInteractionRepository.save(interaction);
        return toResponse(savedInteraction);
    }

    @Transactional(readOnly = true)
    public Page<InteractionResponse> getMyInteractions(int page, int size) {
        User currentUser = userService.getLoggedInUser();

        Page<UserItemInteraction> interactions = userItemInteractionRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), PageRequest.of(page, size));

        return interactions.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<InteractionResponse> getInteractionsByProduct(Long productId, int page, int size) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found");
        }

        Page<UserItemInteraction> interactions = userItemInteractionRepository
                .findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(page, size));

        return interactions.map(this::toResponse);
    }

    @Transactional
    public void logInteraction(User user, Product product, InteractionType interactionType) {
        if (user == null || product == null || interactionType == null) {
            throw new IllegalArgumentException("User, product and interaction type are required");
        }

        UserItemInteraction interaction = UserItemInteraction.builder()
                .user(user)
                .product(product)
                .type(interactionType)
                .build();

        userItemInteractionRepository.save(interaction);
    }

    @Transactional
    public void logInteraction(Long userId, Long productId, InteractionType interactionType) {
        User user = getUserByIdOrThrow(userId);
        Product product = getProductByIdOrThrow(productId);
        logInteraction(user, product, interactionType);
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Product getProductByIdOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private InteractionResponse toResponse(UserItemInteraction interaction) {
        return InteractionResponse.builder()
                .id(interaction.getId())
                .userId(interaction.getUser().getId())
                .userName(interaction.getUser().getName())
                .productId(interaction.getProduct().getId())
                .productName(interaction.getProduct().getName())
                .type(interaction.getType())
                .createdAt(interaction.getCreatedAt())
                .build();
    }
}
