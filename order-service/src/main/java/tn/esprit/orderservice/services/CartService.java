package tn.esprit.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.orderservice.dto.ProductDTO;
import tn.esprit.orderservice.entities.Cart;
import tn.esprit.orderservice.entities.CartItem;
import tn.esprit.orderservice.exception.BusinessException;
import tn.esprit.orderservice.exception.ResourceNotFoundException;
import tn.esprit.orderservice.repositories.CartItemRepository;
import tn.esprit.orderservice.repositories.CartRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Cart Service - migrated from monolith.
 * Changes:
 * - Uses ProductServiceClient (WebClient) to fetch product data instead of direct Product entity access
 * - Uses productId (UUID) instead of Product entity references
 * - Denormalizes product name and thumbnail into CartItem for display
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    @Cacheable(value = "cart", key = "#userId")
    public Cart getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-create cart if it doesn't exist
                    Cart newCart = Cart.builder().userId(userId).build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#userId")
    public Cart addItemToCart(UUID userId, UUID productId, Integer quantity,
                              String selectedVariant, String selectedColor, String selectedSize) {
        Cart cart = getOrCreateCart(userId);

        // Fetch product info from Product Service
        ProductDTO product = productServiceClient.getProduct(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new BusinessException("Product is not available: " + product.getName());
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setPrice(product.getPrice()); // Update to latest price
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(productId)
                    .productName(product.getName())
                    .productThumbnail(product.getThumbnail())
                    .quantity(quantity)
                    .price(product.getPrice())
                    .selectedVariant(selectedVariant)
                    .selectedColor(selectedColor)
                    .selectedSize(selectedSize)
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#userId")
    public Cart updateCartItemQuantity(UUID userId, UUID itemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#userId")
    public Cart removeItemFromCart(UUID userId, UUID itemId) {
        Cart cart = getCartByUserId(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#userId")
    public Cart clearCart(UUID userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setAppliedCouponCode(null);
        return cartRepository.save(cart);
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().userId(userId).build();
                    return cartRepository.save(cart);
                });
    }
}
