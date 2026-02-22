package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.OrderStatus;
import tn.esprit.projetPi.services.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.getOrdersByUser(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable String id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getSellerOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.getOrdersBySeller(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO order = orderService.updateOrderStatus(id, status, userDetails.getUsername(), notes);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<OrderDTO>> updateTracking(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam(required = false) String carrier) {
        OrderDTO order = orderService.updateTrackingNumber(id, trackingNumber, carrier);
        return ResponseEntity.ok(ApiResponse.success("Tracking information updated", order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(id, reason != null ? reason : "Customer requested cancellation");
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted", null));
    }
}
