package tn.esprit.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.orderservice.dto.ApiResponse;
import tn.esprit.orderservice.dto.PageResponse;
import tn.esprit.orderservice.dto.request.OrderRequest;
import tn.esprit.orderservice.dto.response.OrderResponse;
import tn.esprit.orderservice.entities.Order;
import tn.esprit.orderservice.enums.OrderStatus;
import tn.esprit.orderservice.enums.PaymentStatus;
import tn.esprit.orderservice.mapper.OrderMapper;
import tn.esprit.orderservice.services.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Order REST Controller - migrated from monolith.
 * Authentication handled by API Gateway.
 * User info from X-User-Id, X-User-Email headers.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    @GetMapping
    @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(mapper.toOrderResponseList(orders)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByUser(userId, PageRequest.of(page, size));
        Page<OrderResponse> response = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's orders (via gateway header)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestHeader(value = "X-User-Id") String userIdHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userId = UUID.fromString(userIdHeader);
        Page<Order> orders = orderService.getOrdersByUser(userId, PageRequest.of(page, size));
        Page<OrderResponse> response = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByStatus(status, PageRequest.of(page, size));
        Page<OrderResponse> response = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toOrderResponse(order)));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(mapper.toOrderResponse(order)));
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String userEmailHeader,
            @RequestParam(required = false) UUID userId) {
        // Use userId from header (gateway) or request param
        UUID resolvedUserId = userIdHeader != null ? UUID.fromString(userIdHeader) : userId;
        String userEmail = userEmailHeader;

        if (resolvedUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User ID is required"));
        }

        Order order = orderService.createOrderFromCart(
                resolvedUserId, userEmail,
                request.getShippingName(), request.getShippingPhone(),
                request.getShippingAddress(), request.getShippingCity(),
                request.getShippingPostalCode(), request.getShippingCountry(),
                request.getPaymentMethod(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", mapper.toOrderResponse(order)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", mapper.toOrderResponse(order)));
    }

    @PatchMapping("/{id}/payment")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false) String transactionId) {
        Order order = orderService.updatePaymentStatus(id, status, transactionId);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", mapper.toOrderResponse(order)));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue (Admin only)")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getTotalRevenue()));
    }
}
