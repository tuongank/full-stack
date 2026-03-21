package com.project.ecommerce.dto.request;

import com.project.ecommerce.enums.*;
import lombok.Data;

import java.util.Set;

@Data
public class ProductFeatureRequest {
    private SkinType skinType;
    private Set<SkinConcern> skinConcerns;
    private Set<String> ingredients;
    private Set<FreeFrom> freeFrom;
    private Texture texture;
    private UsageTime usageTime;
    private Double phLevel;
    private Boolean pregnancySafe;
}
