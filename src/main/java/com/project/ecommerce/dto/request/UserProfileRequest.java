package com.project.ecommerce.dto.request;

import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.enums.SkinType;
import lombok.Data;

import java.util.Set;

@Data
public class UserProfileRequest {
    private SkinType skinType;
    private Set<SkinConcern> skinConcerns;
    private Set<FreeFrom> avoidIngredients;
    private Integer age;
}
