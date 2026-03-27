package tn.esprit.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.orderservice.dto.ApiResponse;
import tn.esprit.orderservice.dto.request.CartItemRequest;
import tn.esprit.orderservice.dto.response.CartResponse;
import tn.esprit.orderservice.entities.Cart;
import tn.esprit.orderservice.mapper.OrderMapper;
import tn.esprit.orderservice.services.CartService;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart endpoints")
public class CartController {

    private final CartService cartService;
    private final OrderMapper mapper;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCartResponse(cart)));
    }

    @GetMapping("/my-cart")
    @Operation(summary = "Get current user's cart (via gateway header)")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @RequestHeader(value = "X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCartResponse(cart)));
    }

    @PostMapping("/{userId}/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @PathVariable UUID userId,
            @Valid @RequestBody CartItemRequest request) {
        Cart cart = cartService.addItemToCart(
                userId, request.getProductId(), request.getQuantity(),
                request.getSelectedVariant(), request.getSelectedColor(), request.getSelectedSize());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", mapper.toCartResponse(cart)));
    }

    @PutMapping("/{userId}/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", mapper.toCartResponse(cart)));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID itemId) {
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", mapper.toCartResponse(cart)));
    }

    @DeleteMapping("/{userId}/clear")
    @Operation(summary = "Clear cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@PathVariable UUID userId) {
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", mapper.toCartResponse(cart)));
    }
}
