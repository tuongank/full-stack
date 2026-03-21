package com.project.ecommerce.dto.request;

import com.project.ecommerce.enums.InteractionType;
import lombok.Data;

@Data
public class InteractionRequest {
    private Long productId;
    private InteractionType type;
}
