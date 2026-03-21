package com.project.ecommerce.dto.response;

import com.project.ecommerce.enums.InteractionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteractionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private InteractionType type;
    private LocalDateTime createdAt;
}
