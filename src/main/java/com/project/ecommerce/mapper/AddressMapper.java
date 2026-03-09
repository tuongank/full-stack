package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.response.AddressResponse;
import com.project.ecommerce.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toAddressResponse(Address address);
}
