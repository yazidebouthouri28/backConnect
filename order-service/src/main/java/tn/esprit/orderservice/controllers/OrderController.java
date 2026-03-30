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
        // Mapper les entités vers DTO
        List<OrderResponse> dtoList = mapper.toOrderResponseList(orders);
        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByUser(userId, PageRequest.of(page, size));
        // Mapper les entités vers DTO
        Page<OrderResponse> dtoPage = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(dtoPage)));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's orders (via gateway header)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestHeader(value = "X-User-Id") String userIdHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userId = UUID.fromString(userIdHeader);
        Page<Order> orders = orderService.getOrdersByUser(userId, PageRequest.of(page, size));
        Page<OrderResponse> dtoPage = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(dtoPage)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByStatus(status, PageRequest.of(page, size));
        Page<OrderResponse> dtoPage = orders.map(mapper::toOrderResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(dtoPage)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        OrderResponse dto = mapper.toOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        OrderResponse dto = mapper.toOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String userEmailHeader,
            @RequestParam(required = false) UUID userId) {

        UUID resolvedUserId = userIdHeader != null ? UUID.fromString(userIdHeader) : userId;
        if (resolvedUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User ID is required"));
        }

        Order order = orderService.createOrderFromCart(
                resolvedUserId, userEmailHeader,
                request.getShippingName(), request.getShippingPhone(),
                request.getShippingAddress(), request.getShippingCity(),
                request.getShippingPostalCode(), request.getShippingCountry(),
                request.getPaymentMethod(), request.getNotes()
        );

        OrderResponse dto = mapper.toOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {

        Order order = orderService.updateOrderStatus(id, status);
        OrderResponse dto = mapper.toOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", dto));
    }

    @PatchMapping("/{id}/payment")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false) String transactionId) {

        Order order = orderService.updatePaymentStatus(id, status, transactionId);
        OrderResponse dto = mapper.toOrderResponse(order);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", dto));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue (Admin only)")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
        BigDecimal revenue = orderService.getTotalRevenue();
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }
}