package com.project.ecommerce.dto.response;

import com.project.ecommerce.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
