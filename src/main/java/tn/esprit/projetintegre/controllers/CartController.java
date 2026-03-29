package tn.esprit.projetintegre.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.ApiResponse;
import tn.esprit.projetintegre.dto.request.CartItemRequest;
import tn.esprit.projetintegre.dto.response.CartResponse;
import tn.esprit.projetintegre.entities.Cart;
import tn.esprit.projetintegre.entities.Product;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.mapper.DtoMapper;
import tn.esprit.projetintegre.repositories.ProductRepository;
import tn.esprit.projetintegre.repositories.UserRepository;
import tn.esprit.projetintegre.services.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final CartService cartService;
    private final DtoMapper dtoMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toCartResponse(cart)));
    }

    @GetMapping("/user/{userId}/ensure")
    @Operation(summary = "Get or create cart for user")
    public ResponseEntity<ApiResponse<CartResponse>> getOrCreateCart(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Cart cart = cartService.getOrCreateCart(user);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toCartResponse(cart)));
    }

    @PostMapping("/{userId}/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        Cart cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", dtoMapper.toCartResponse(cart)));
    }

    @PostMapping("/user/{userId}/items")
    @Operation(summary = "Add item to cart (body)")
    public ResponseEntity<ApiResponse<CartResponse>> addItemWithBody(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        Cart cart = cartService.addItem(user, product, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", dtoMapper.toCartResponse(cart)));
    }

    @PutMapping("/{userId}/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", dtoMapper.toCartResponse(cart)));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    @Operation(summary = "Remove item from cart (by user)")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", dtoMapper.toCartResponse(cart)));
    }

    @DeleteMapping("/cart/{cartId}/items/{itemId}")
    @Operation(summary = "Remove item from cart (by cart id)")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemByCartId(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        Cart cart = cartService.removeItemByCartId(cartId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", dtoMapper.toCartResponse(cart)));
    }

    @PostMapping("/user/{userId}/coupon")
    @Operation(summary = "Apply coupon code to cart")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(
            @PathVariable Long userId,
            @RequestParam String code) {
        Cart cart = cartService.applyCouponToCart(userId, code);
        return ResponseEntity.ok(ApiResponse.success("Coupon applied", dtoMapper.toCartResponse(cart)));
    }

    @DeleteMapping("/{userId}/clear")
    @Operation(summary = "Clear cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", dtoMapper.toCartResponse(cart)));
    }
}
