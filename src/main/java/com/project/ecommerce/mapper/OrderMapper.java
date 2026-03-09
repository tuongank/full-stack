package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.OrderResponse;
import com.project.ecommerce.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
}
