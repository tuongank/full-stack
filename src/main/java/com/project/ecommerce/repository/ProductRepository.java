package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findProductsByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}
