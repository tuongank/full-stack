package com.project.ecommerce.dto.response;

import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.enums.SkinType;
import com.project.ecommerce.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserDetailResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    
    // Profile info
    private SkinType skinType;
    private Set<SkinConcern> skinConcerns;
    private Set<FreeFrom> avoidIngredients;
    private Integer age;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
