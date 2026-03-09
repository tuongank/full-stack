package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.CategoryResponse;
import com.project.ecommerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
