package com.project.ecommerce.dto.response;

import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.enums.SkinType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String email;
    private SkinType skinType;
    private Set<SkinConcern> skinConcerns;
    private Set<FreeFrom> avoidIngredients;
    private Integer age;
}
