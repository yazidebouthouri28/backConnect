package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CartDTO;
import tn.esprit.projetPi.dto.CartItemDTO;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CartRepository;
import tn.esprit.projetPi.repositories.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartDTO getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));
        return toDTO(cart);
    }

    public CartDTO addItem(String userId, CartItemDTO itemDTO) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));

        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDTO.getProductId()));

        // Check if item already exists
        boolean found = false;
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(itemDTO.getProductId())) {
                item.setQuantity(item.getQuantity() + itemDTO.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(itemDTO.getQuantity());
            newItem.setImage(product.getImages() != null && !product.getImages().isEmpty() 
                    ? product.getImages().get(0) : null);
            newItem.setType(itemDTO.getType() != null ? itemDTO.getType() : OrderType.PURCHASE);
            newItem.setRentalDays(itemDTO.getRentalDays());
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toDTO(saved);
    }

    public CartDTO updateItemQuantity(String userId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    cart.getItems().remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Item not found in cart: " + productId);
        }

        updateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toDTO(saved);
    }

    public CartDTO removeItem(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        updateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toDTO(saved);
    }

    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    private Cart createEmptyCart(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
        cart.setTotalAmount(total);
    }

    private CartDTO toDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setTotalAmount(cart.getTotalAmount());
        dto.setItemCount(cart.getItems() != null ? cart.getItems().size() : 0);
        
        if (cart.getItems() != null) {
            dto.setItems(cart.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private CartItemDTO toItemDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setImage(item.getImage());
        dto.setType(item.getType());
        dto.setRentalDays(item.getRentalDays());
        return dto;
    }
}
