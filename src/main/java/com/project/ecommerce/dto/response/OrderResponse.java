package com.project.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private BigDecimal totalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;
    private AddressResponse deliveryAddress;
    private PaymentResponse paymentInfo;
    private List<OrderItemResponse> orderItems;
}
