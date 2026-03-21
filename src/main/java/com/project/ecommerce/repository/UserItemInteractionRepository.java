package com.project.ecommerce.repository;

import com.project.ecommerce.entity.UserItemInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemInteractionRepository extends JpaRepository<UserItemInteraction, Long> {

    Page<UserItemInteraction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<UserItemInteraction> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    List<UserItemInteraction> findByUserId(Long userId);

    List<UserItemInteraction> findByProductId(Long productId);
}
