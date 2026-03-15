package com.project.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyCodeRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String email;
}
