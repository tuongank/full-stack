package com.project.ecommerce.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressRequest {
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String ward;
    private String district;
    private Boolean isDefault;
}
