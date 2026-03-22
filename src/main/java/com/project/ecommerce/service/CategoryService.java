package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.CategoryRequest;
import com.project.ecommerce.dto.response.CategoryResponse;
import com.project.ecommerce.dto.response.ProductResponse;
import com.project.ecommerce.entity.Category;
import com.project.ecommerce.exception.InvalidCredentialsException;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.mapper.CategoryMapper;
import com.project.ecommerce.mapper.ProductMapper;
import com.project.ecommerce.repository.CategoryRepository;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new InvalidCredentialsException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .build();
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    public Page<CategoryResponse> getAllCategory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toCategoryResponse);
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        return categoryMapper.toCategoryResponse(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(() -> new NotFoundException("Category name not found"));
        return categoryMapper.toCategoryResponse(category);
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new InvalidCredentialsException("Category name already exists");
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));

        category.setName(request.getName());
        categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        // Verify category exists
        categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findProductByCategoryId(categoryId, pageable)
                .map(productMapper::toProductResponse);
    }
}
