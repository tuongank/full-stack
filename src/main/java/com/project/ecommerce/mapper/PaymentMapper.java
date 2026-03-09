package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.PaymentResponse;
import com.project.ecommerce.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);
}
