package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.AddressRequest;
import com.project.ecommerce.dto.response.AddressResponse;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@RequestBody AddressRequest request) {
        AddressResponse addressResponse = addressService.createAddress(request);
        ApiResponse<AddressResponse> apiResponse = ApiResponse.<AddressResponse>builder()
                .status(200)
                .message("Address created successfully")
                .data(addressResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddress() {
        List<AddressResponse> addressResponses = addressService.getUserAddresses();
        ApiResponse<List<AddressResponse>> apiResponse = ApiResponse.<List<AddressResponse>>builder()
                .status(200)
                .message("User addresses retrieved successfully")
                .data(addressResponses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        ApiResponse<AddressResponse> apiResponse = ApiResponse.<AddressResponse>builder()
                .status(200)
                .message("Address deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
