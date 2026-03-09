package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.OrderItemResponse;
import com.project.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "productImage")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
