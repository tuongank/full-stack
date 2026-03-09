package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.UserResponse;
import com.project.ecommerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
