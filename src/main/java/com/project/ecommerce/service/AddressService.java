package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.AddressRequest;
import com.project.ecommerce.dto.response.AddressResponse;
import com.project.ecommerce.entity.Address;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.mapper.AddressMapper;
import com.project.ecommerce.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final AddressMapper addressMapper;

    public AddressResponse createAddress(AddressRequest request) {
        User user = userService.getLoggedInUser();

        if (request.getIsDefault() != null && request.getIsDefault()) {
            List<Address> userAddresses = addressRepository.findByUserId(user.getId());
            userAddresses.forEach(address -> address.setIsDefault(false));
            addressRepository.saveAll(userAddresses);
        }

        Address address = Address.builder()
                .user(user)
                .street(request.getStreet())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();
        addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    public List<AddressResponse> getUserAddresses() {
        User user = userService.getLoggedInUser();
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        return addresses.stream().map(addressMapper::toAddressResponse).toList();
    }

    public void deleteAddress(Long addressId) {
        User user = userService.getLoggedInUser();
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId()).orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }
}
