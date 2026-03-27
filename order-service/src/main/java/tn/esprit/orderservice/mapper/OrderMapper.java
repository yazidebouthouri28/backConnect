package tn.esprit.orderservice.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.orderservice.dto.response.*;
import tn.esprit.orderservice.entities.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Mapper for Order Service entities.
 */
@Component
public class OrderMapper {

    // ── Order Mapping ──

    public OrderResponse toOrderResponse(Order entity) {
        if (entity == null) return null;
        return OrderResponse.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .userEmail(entity.getUserEmail())
                .items(toOrderItemResponseList(entity.getItems()))
                .subtotal(entity.getSubtotal())
                .discountAmount(entity.getDiscountAmount())
                .taxAmount(entity.getTaxAmount())
                .shippingCost(entity.getShippingCost())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .paymentMethod(entity.getPaymentMethod())
                .shippingName(entity.getShippingName())
                .shippingAddress(entity.getShippingAddress())
                .shippingCity(entity.getShippingCity())
                .shippingCountry(entity.getShippingCountry())
                .shippingPhone(entity.getShippingPhone())
                .trackingNumber(entity.getTrackingNumber())
                .notes(entity.getNotes())
                .orderedAt(entity.getOrderedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deliveredAt(entity.getDeliveredAt())
                .build();
    }

    public List<OrderResponse> toOrderResponseList(List<Order> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toOrderResponse).collect(Collectors.toList());
    }

    public OrderItemResponse toOrderItemResponse(OrderItem entity) {
        if (entity == null) return null;
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productThumbnail(entity.getProductThumbnail())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .subtotal(entity.getTotalPrice())
                .build();
    }

    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toOrderItemResponse).collect(Collectors.toList());
    }

    // ── Cart Mapping ──

    public CartResponse toCartResponse(Cart entity) {
        if (entity == null) return null;
        BigDecimal total = entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal discount = entity.getDiscountAmount() != null ? entity.getDiscountAmount() : BigDecimal.ZERO;
        return CartResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(toCartItemResponseList(entity.getItems()))
                .totalAmount(total)
                .discountAmount(discount)
                .finalAmount(total.subtract(discount))
                .totalItems(entity.getItems() != null ? entity.getItems().size() : 0)
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItem entity) {
        if (entity == null) return null;
        BigDecimal price = entity.getPrice() != null ? entity.getPrice() : BigDecimal.ZERO;
        int qty = entity.getQuantity() != null ? entity.getQuantity() : 0;
        return CartItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productThumbnail(entity.getProductThumbnail())
                .unitPrice(price)
                .quantity(qty)
                .subtotal(price.multiply(BigDecimal.valueOf(qty)))
                .build();
    }

    public List<CartItemResponse> toCartItemResponseList(List<CartItem> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toCartItemResponse).collect(Collectors.toList());
    }

    // ── Shipping Address Mapping ──

    public ShippingAddressResponse toShippingAddressResponse(ShippingAddress entity) {
        if (entity == null) return null;
        return ShippingAddressResponse.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .recipientName(entity.getRecipientName())
                .phone(entity.getPhone())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .deliveryInstructions(entity.getDeliveryInstructions())
                .isDefault(entity.getIsDefault())
                .isActive(entity.getIsActive())
                .userId(entity.getUserId())
                .fullAddress(entity.getFullAddress())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<ShippingAddressResponse> toShippingAddressResponseList(List<ShippingAddress> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toShippingAddressResponse).collect(Collectors.toList());
    }
}
