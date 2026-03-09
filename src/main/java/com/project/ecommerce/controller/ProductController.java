package com.project.ecommerce.controller;

import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.ProductResponse;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestParam Long categoryId,
                                                                    @RequestParam("image") MultipartFile image,
                                                                    @RequestParam String name,
                                                                    @RequestParam String description,
                                                                    @RequestParam BigDecimal price,
                                                                    @RequestParam Integer stock) {
        ProductResponse productResponse = productService.createProduct(categoryId, image, name, description, price, stock);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(201)
                .message("Product created successfully")
                .data(productResponse)
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> productResponsePage = productService.getAllProducts(page, size);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Products retrieved successfully")
                .data(productResponsePage.getContent())
                .totalPages(productResponsePage.getTotalPages())
                .totalElements(productResponsePage.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> productResponse = productService.getProductsByCategory(categoryId, page, size);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Products retrieved successfully")
                .data(productResponse.getContent())
                .totalPages(productResponse.getTotalPages())
                .totalElements(productResponse.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Product retrieved successfully")
                .data(productResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
                                                                    @RequestParam Long categoryId,
                                                                    @RequestParam(required = false) MultipartFile image,
                                                                    @RequestParam String name,
                                                                    @RequestParam String description,
                                                                    @RequestParam BigDecimal price,
                                                                    @RequestParam Integer stock) {
        ProductResponse productResponse = productService.updateProduct(id, categoryId, image, name, description, price, stock);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Product updated successfully")
                .data(productResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Product deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String product, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> productResponsePage = productService.searchProductByName(product, page, size);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Products retrieved successfully")
                .data(productResponsePage.getContent())
                .totalPages(productResponsePage.getTotalPages())
                .totalElements(productResponsePage.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }
}
