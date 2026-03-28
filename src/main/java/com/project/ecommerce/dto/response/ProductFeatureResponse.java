package com.project.ecommerce.dto.response;

import com.project.ecommerce.enums.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ProductFeatureResponse {
    private Long id;
    private Long productId;
    private String productName;
    private SkinType skinType;
    private Set<SkinConcern> skinConcerns;
    private Set<String> ingredients;
    private Set<FreeFrom> freeFrom;
    private Texture texture;
    private UsageTime usageTime;
    private Double phLevel;
    private Boolean pregnancySafe;
    private String brand;
}
