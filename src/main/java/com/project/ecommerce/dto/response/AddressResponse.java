package com.project.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private String street;
    private String city;
    private String ward;
    private String district;
    private String receiverName;
    private Boolean isDefault;
}
