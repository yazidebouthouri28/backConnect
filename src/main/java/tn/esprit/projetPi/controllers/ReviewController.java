package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.services.ProductReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ProductReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getProductReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductReviewDTO> reviews = reviewService.getReviewsByProduct(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/product/{productId}/top")
    public ResponseEntity<ApiResponse<List<ProductReviewDTO>>> getTopReviews(@PathVariable String productId) {
        List<ProductReviewDTO> reviews = reviewService.getTopReviewsForProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getUserReviews(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductReviewDTO> reviews = reviewService.getReviewsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductReviewDTO> reviews = reviewService.getReviewsByUser(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> getReview(@PathVariable String id) {
        ProductReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductReviewDTO>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        ProductReviewDTO review = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Review submitted for approval", review));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> updateReview(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        ProductReviewDTO review = reviewService.updateReview(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Review updated", review));
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> markHelpful(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProductReviewDTO review = reviewService.markHelpful(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PostMapping("/{id}/seller-response")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> addSellerResponse(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String response) {
        ProductReviewDTO review = reviewService.addSellerResponse(id, userDetails.getUsername(), response);
        return ResponseEntity.ok(ApiResponse.success("Response added", review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername(), false);
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductReviewDTO>>> getFeaturedReviews() {
        List<ProductReviewDTO> reviews = reviewService.getFeaturedReviews();
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
