package com.project.ecommerce.service;

import com.project.ecommerce.dto.response.ProductResponse;
import com.project.ecommerce.entity.Category;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.InteractionType;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.mapper.ProductMapper;
import com.project.ecommerce.repository.CategoryRepository;
import com.project.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;
    private final UserItemInteractionService userItemInteractionService;
    private final UserService userService;

    @Transactional
    public ProductResponse createProduct(Long CategoryId, MultipartFile image, String name, String description, BigDecimal price, Integer stock) {
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Upload image to Cloudinary and get the URL
        String imageUrl = cloudinaryService.uploadImage(image);

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .stock(stock)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();
        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(int page, int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page, size));
        return products.map(productMapper::toProductResponse);
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
                throw new NotFoundException("Category not found");
        }
        Page<Product> products = productRepository.findProductByCategoryId(categoryId, PageRequest.of(page, size));
        return products.map(productMapper::toProductResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price, Integer stock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));


        if (categoryId != null) {
             Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
             product.setCategory(category);
        }

        // Upload new image to Cloudinary and get the URL
        if (image != null && !image.isEmpty()) {
            String oldImageUrl = product.getImageUrl();
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) { // Delete old image from Cloudinary
                cloudinaryService.deleteImage(oldImageUrl);
            }
            String newImageUrl = cloudinaryService.uploadImage(image); // Upload new image and get URL
            product.setImageUrl(newImageUrl);
        }

        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (stock != null) product.setStock(stock);

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        User user = userService.getLoggedInUser();
        userItemInteractionService.logInteraction(user, product, InteractionType.VIEW);
        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> searchProductByName(String name, int page, int size) {
        Page<Product> products = productRepository.findProductsByNameContainingOrDescriptionContaining(name, name, PageRequest.of(page, size));
        return products.map(productMapper::toProductResponse);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Delete image from Cloudinary
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            cloudinaryService.deleteImage(product.getImageUrl());
        }
        productRepository.delete(product);
    }
}
