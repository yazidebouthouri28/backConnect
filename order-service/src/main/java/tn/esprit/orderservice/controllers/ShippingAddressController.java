package tn.esprit.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.orderservice.dto.ApiResponse;
import tn.esprit.orderservice.dto.response.ShippingAddressResponse;
import tn.esprit.orderservice.dto.request.ShippingAddressRequest;
import tn.esprit.orderservice.entities.ShippingAddress;
import tn.esprit.orderservice.mapper.OrderMapper;
import tn.esprit.orderservice.services.ShippingAddressService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipping-addresses")
@RequiredArgsConstructor
@Tag(name = "Shipping Addresses", description = "Shipping address management")
public class ShippingAddressController {

    private final ShippingAddressService addressService;
    private final OrderMapper mapper;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's shipping addresses")
    public ResponseEntity<ApiResponse<List<ShippingAddressResponse>>> getByUserId(@PathVariable UUID userId) {
        List<ShippingAddress> addresses = addressService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toShippingAddressResponseList(addresses)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> getById(@PathVariable UUID id) {
        ShippingAddress address = addressService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toShippingAddressResponse(address)));
    }

    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Get user's default address")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> getDefaultByUserId(@PathVariable UUID userId) {
        ShippingAddress address = addressService.getDefaultByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toShippingAddressResponse(address)));
    }

    @PostMapping
    @Operation(summary = "Create a new address")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> create(
            @Valid @RequestBody ShippingAddressRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        UUID userId = request.getUserId() != null ? request.getUserId()
                : (userIdHeader != null ? UUID.fromString(userIdHeader) : null);
        ShippingAddress address = addressService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully", mapper.toShippingAddressResponse(address)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ShippingAddressRequest request) {
        ShippingAddress address = addressService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated", mapper.toShippingAddressResponse(address)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        addressService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }
}
