package com.project.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String content;
    private int rating;

    private Long productId;
    private String productName;

    private Long userId;
    private String userName;

    private LocalDateTime createdAt;
}
