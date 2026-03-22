package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.OrderResponse;
import com.project.ecommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, PaymentMapper.class, OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(source = "address", target = "deliveryAddress")
    @Mapping(source = "payment", target = "paymentInfo")
    @Mapping(source = "orderItemList", target = "orderItems")
    OrderResponse toOrderResponse(Order order);
}
