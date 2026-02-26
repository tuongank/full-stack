package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {
    private Long id;
    private BigDecimal amount;
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL"
    private String status; // e.g., "PENDING", "COMPLETED", "
    private OrderDto order;
}
