package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.WishlistDTO;
import tn.esprit.projetPi.dto.WishlistItemDTO;
import tn.esprit.projetPi.entities.Product;
import tn.esprit.projetPi.entities.Wishlist;
import tn.esprit.projetPi.entities.WishlistItem;
import tn.esprit.projetPi.exception.DuplicateResourceException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.ProductRepository;
import tn.esprit.projetPi.repositories.WishlistRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public List<WishlistDTO> getUserWishlists(String userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WishlistDTO getDefaultWishlist(String userId) {
        return wishlistRepository.findByUserIdAndIsDefault(userId, true)
                .map(this::toDTO)
                .orElseGet(() -> createWishlist(userId, "My Wishlist", true));
    }

    public WishlistDTO getWishlistById(String id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found with id: " + id));
        return toDTO(wishlist);
    }

    public WishlistDTO createWishlist(String userId, String name, boolean isDefault) {
        if (wishlistRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateResourceException("Wishlist with name '" + name + "' already exists");
        }

        // If this is default, unset other defaults
        if (isDefault) {
            wishlistRepository.findByUserIdAndIsDefault(userId, true).ifPresent(w -> {
                w.setIsDefault(false);
                wishlistRepository.save(w);
            });
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setName(name);
        wishlist.setIsDefault(isDefault);
        wishlist.setIsPublic(false);
        wishlist.setItems(new ArrayList<>());
        wishlist.setCreatedAt(LocalDateTime.now());

        Wishlist saved = wishlistRepository.save(wishlist);
        log.info("Wishlist created: {} for user {}", saved.getId(), userId);
        return toDTO(saved);
    }

    public WishlistDTO addProductToWishlist(String userId, String productId, String wishlistId, String notes) {
        Wishlist wishlist;
        if (wishlistId != null) {
            wishlist = wishlistRepository.findById(wishlistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
            if (!wishlist.getUserId().equals(userId)) {
                throw new IllegalStateException("You can only add to your own wishlists");
            }
        } else {
            wishlist = wishlistRepository.findByUserIdAndIsDefault(userId, true)
                    .orElseGet(() -> {
                        Wishlist newWishlist = new Wishlist();
                        newWishlist.setUserId(userId);
                        newWishlist.setName("My Wishlist");
                        newWishlist.setIsDefault(true);
                        newWishlist.setIsPublic(false);
                        newWishlist.setItems(new ArrayList<>());
                        newWishlist.setCreatedAt(LocalDateTime.now());
                        return wishlistRepository.save(newWishlist);
                    });
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if product already in wishlist
        if (wishlist.getItems() != null && 
            wishlist.getItems().stream().anyMatch(i -> i.getProductId().equals(productId))) {
            throw new DuplicateResourceException("Product already in wishlist");
        }

        WishlistItem item = new WishlistItem();
        item.setProductId(productId);
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage() != null ? product.getMainImage() : 
                (product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null));
        item.setPriceWhenAdded(product.getPrice());
        item.setCurrentPrice(product.getPrice());
        item.setInStock(product.getInStock());
        item.setAddedAt(LocalDateTime.now());
        item.setNotes(notes);

        if (wishlist.getItems() == null) {
            wishlist.setItems(new ArrayList<>());
        }
        wishlist.getItems().add(item);
        wishlist.setUpdatedAt(LocalDateTime.now());

        // Update product wishlist count
        product.setWishlistCount((product.getWishlistCount() != null ? product.getWishlistCount() : 0) + 1);
        productRepository.save(product);

        Wishlist saved = wishlistRepository.save(wishlist);
        log.info("Product {} added to wishlist {} by user {}", productId, saved.getId(), userId);
        return toDTO(saved);
    }

    public WishlistDTO removeProductFromWishlist(String userId, String productId, String wishlistId) {
        Wishlist wishlist;
        if (wishlistId != null) {
            wishlist = wishlistRepository.findById(wishlistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        } else {
            wishlist = wishlistRepository.findByUserIdAndIsDefault(userId, true)
                    .orElseThrow(() -> new ResourceNotFoundException("Default wishlist not found"));
        }

        if (!wishlist.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only modify your own wishlists");
        }

        if (wishlist.getItems() != null) {
            boolean removed = wishlist.getItems().removeIf(i -> i.getProductId().equals(productId));
            if (removed) {
                wishlist.setUpdatedAt(LocalDateTime.now());
                wishlistRepository.save(wishlist);

                // Update product wishlist count
                productRepository.findById(productId).ifPresent(p -> {
                    p.setWishlistCount(Math.max(0, (p.getWishlistCount() != null ? p.getWishlistCount() : 1) - 1));
                    productRepository.save(p);
                });
            }
        }

        return toDTO(wishlist);
    }

    public WishlistDTO updateWishlist(String userId, String wishlistId, String name, Boolean isPublic) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only update your own wishlists");
        }

        if (name != null) wishlist.setName(name);
        if (isPublic != null) wishlist.setIsPublic(isPublic);
        wishlist.setUpdatedAt(LocalDateTime.now());

        return toDTO(wishlistRepository.save(wishlist));
    }

    public void deleteWishlist(String userId, String wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own wishlists");
        }

        if (Boolean.TRUE.equals(wishlist.getIsDefault())) {
            throw new IllegalStateException("Cannot delete default wishlist");
        }

        wishlistRepository.deleteById(wishlistId);
    }

    public boolean isProductInWishlist(String userId, String productId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .anyMatch(w -> w.getItems() != null && 
                        w.getItems().stream().anyMatch(i -> i.getProductId().equals(productId)));
    }

    public WishlistDTO refreshWishlistPrices(String wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        if (wishlist.getItems() != null) {
            for (WishlistItem item : wishlist.getItems()) {
                productRepository.findById(item.getProductId()).ifPresent(p -> {
                    item.setCurrentPrice(p.getPrice());
                    item.setInStock(p.getInStock());
                    item.setProductName(p.getName());
                });
            }
            wishlist.setUpdatedAt(LocalDateTime.now());
            wishlistRepository.save(wishlist);
        }

        return toDTO(wishlist);
    }

    private WishlistDTO toDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUserId())
                .name(wishlist.getName())
                .isDefault(wishlist.getIsDefault())
                .isPublic(wishlist.getIsPublic())
                .items(wishlist.getItems() != null ? wishlist.getItems().stream()
                        .map(this::toItemDTO).collect(Collectors.toList()) : new ArrayList<>())
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }

    private WishlistItemDTO toItemDTO(WishlistItem item) {
        return WishlistItemDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .priceWhenAdded(item.getPriceWhenAdded())
                .currentPrice(item.getCurrentPrice())
                .inStock(item.getInStock())
                .addedAt(item.getAddedAt())
                .notes(item.getNotes())
                .build();
    }
}
