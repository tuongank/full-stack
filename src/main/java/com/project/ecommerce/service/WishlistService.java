package com.project.ecommerce.service;

import com.project.ecommerce.dto.response.WishlistResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.Wishlist;
import com.project.ecommerce.enums.InteractionType;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final UserItemInteractionService userItemInteractionService;

    @Transactional
    public WishlistResponse addToWishlist(Long productId) {
        User currentUser = userService.getLoggedInUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new IllegalStateException("Product already in wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(currentUser)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);

        // Log interaction for recommendation system
        userItemInteractionService.logInteraction(currentUser, product, InteractionType.WISHLIST);

        return toResponse(saved);
    }

    @Transactional
    public void removeFromWishlist(Long productId) {
        User currentUser = userService.getLoggedInUser();

        if (!wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new NotFoundException("Product not found in wishlist");
        }

        wishlistRepository.deleteByUserIdAndProductId(currentUser.getId(), productId);
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponse> getMyWishlist(int page, int size) {
        User currentUser = userService.getLoggedInUser();
        Page<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByAddedAtDesc(
                currentUser.getId(), PageRequest.of(page, size));
        return wishlists.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(Long productId) {
        User currentUser = userService.getLoggedInUser();
        return wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId);
    }

    @Transactional(readOnly = true)
    public long getWishlistCount() {
        User currentUser = userService.getLoggedInUser();
        return wishlistRepository.countByUserId(currentUser.getId());
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .productPrice(product.getPrice())
                .productStock(product.getStock())
                .addedAt(wishlist.getAddedAt())
                .build();
    }
}
