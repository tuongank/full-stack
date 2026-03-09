package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.ProductResponse;
import com.project.ecommerce.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
}
