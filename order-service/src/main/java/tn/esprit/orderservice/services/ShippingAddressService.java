package tn.esprit.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.orderservice.dto.request.ShippingAddressRequest;
import tn.esprit.orderservice.entities.ShippingAddress;
import tn.esprit.orderservice.exception.ResourceNotFoundException;
import tn.esprit.orderservice.repositories.ShippingAddressRepository;

import java.util.List;
import java.util.UUID;

/**
 * Shipping Address Service - migrated from monolith.
 * Changes: User entity replaced with userId (UUID).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingAddressService {

    private final ShippingAddressRepository addressRepository;

    public List<ShippingAddress> getByUserId(UUID userId) {
        return addressRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public ShippingAddress getById(UUID id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found with id: " + id));
    }

    public ShippingAddress getDefaultByUserId(UUID userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Default address not found for user: " + userId));
    }

    public ShippingAddress create(ShippingAddressRequest request, UUID userId) {
        // If this is set as default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        ShippingAddress address = ShippingAddress.builder()
                .userId(userId)
                .label(request.getLabel())
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .deliveryInstructions(request.getDeliveryInstructions())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .isActive(true)
                .build();

        return addressRepository.save(address);
    }

    public ShippingAddress update(UUID id, ShippingAddressRequest request) {
        ShippingAddress address = getById(id);

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(address.getUserId())
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        if (request.getLabel() != null) address.setLabel(request.getLabel());
        if (request.getRecipientName() != null) address.setRecipientName(request.getRecipientName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getAddressLine1() != null) address.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) address.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getDeliveryInstructions() != null) address.setDeliveryInstructions(request.getDeliveryInstructions());
        if (request.getIsDefault() != null) address.setIsDefault(request.getIsDefault());

        return addressRepository.save(address);
    }

    public void delete(UUID id) {
        ShippingAddress address = getById(id);
        address.setIsActive(false);
        addressRepository.save(address);
    }
}
