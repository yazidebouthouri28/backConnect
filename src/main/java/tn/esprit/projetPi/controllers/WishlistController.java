package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.WishlistDTO;
import tn.esprit.projetPi.services.WishlistService;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistDTO>>> getMyWishlists(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<WishlistDTO> wishlists = wishlistService.getUserWishlists(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(wishlists));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<WishlistDTO>> getDefaultWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        WishlistDTO wishlist = wishlistService.getDefaultWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WishlistDTO>> getWishlist(@PathVariable String id) {
        WishlistDTO wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistDTO>> createWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean isDefault) {
        WishlistDTO wishlist = wishlistService.createWishlist(userDetails.getUsername(), name, isDefault);
        return ResponseEntity.ok(ApiResponse.success("Wishlist created", wishlist));
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<WishlistDTO>> addProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId,
            @RequestParam(required = false) String wishlistId,
            @RequestParam(required = false) String notes) {
        WishlistDTO wishlist = wishlistService.addProductToWishlist(
                userDetails.getUsername(), productId, wishlistId, notes);
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", wishlist));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<WishlistDTO>> removeProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId,
            @RequestParam(required = false) String wishlistId) {
        WishlistDTO wishlist = wishlistService.removeProductFromWishlist(
                userDetails.getUsername(), productId, wishlistId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", wishlist));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WishlistDTO>> updateWishlist(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isPublic) {
        WishlistDTO wishlist = wishlistService.updateWishlist(
                userDetails.getUsername(), id, name, isPublic);
        return ResponseEntity.ok(ApiResponse.success("Wishlist updated", wishlist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWishlist(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.deleteWishlist(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Wishlist deleted", null));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> isProductInWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId) {
        boolean inWishlist = wishlistService.isProductInWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(ApiResponse.success(inWishlist));
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<ApiResponse<WishlistDTO>> refreshPrices(@PathVariable String id) {
        WishlistDTO wishlist = wishlistService.refreshWishlistPrices(id);
        return ResponseEntity.ok(ApiResponse.success("Prices refreshed", wishlist));
    }
}
