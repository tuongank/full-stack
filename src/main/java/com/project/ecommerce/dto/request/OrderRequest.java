package com.project.ecommerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {
    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> orderItems;
}
