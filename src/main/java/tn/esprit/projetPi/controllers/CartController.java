package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.CartDTO;
import tn.esprit.projetPi.dto.CartItemDTO;
import tn.esprit.projetPi.services.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemDTO itemDTO) {
        String userId = (String) authentication.getPrincipal();
        CartDTO cart = cartService.addItem(userId, itemDTO);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateItemQuantity(
            Authentication authentication,
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        String userId = (String) authentication.getPrincipal();
        CartDTO cart = cartService.updateItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(
            Authentication authentication,
            @PathVariable String productId) {
        String userId = (String) authentication.getPrincipal();
        CartDTO cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
