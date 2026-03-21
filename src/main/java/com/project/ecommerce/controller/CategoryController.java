package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.CategoryRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.CategoryResponse;
import com.project.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api//category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .status(201)
                .message("Category created successfully")
                .data(categoryResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByName(@RequestParam String name) {
        CategoryResponse categoryResponse = categoryService.getCategoryByName(name);
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Category retrieved successfully")
                .data(categoryResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(@RequestParam (defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<CategoryResponse> responses = categoryService.getAllCategory(page, size);
        ApiResponse<List<CategoryResponse>> response = ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Categories retrieved successfully")
                .data(responses.getContent())
                .totalPages(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long categoryId, @RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, categoryRequest);
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Category updated successfully")
                .data(categoryResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        ApiResponse<?> response = ApiResponse.builder()
                .status(200)
                .message("Category deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
