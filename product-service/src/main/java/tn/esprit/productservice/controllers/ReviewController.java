package tn.esprit.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.productservice.dto.ApiResponse;
import tn.esprit.productservice.dto.PageResponse;
import tn.esprit.productservice.dto.request.ReviewRequest;
import tn.esprit.productservice.dto.response.ReviewResponse;
import tn.esprit.productservice.entities.ProductReview;
import tn.esprit.productservice.services.ReviewService;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product review endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ReviewResponse> directly — no mapper call needed
        Page<ReviewResponse> response = reviewService.getReviewsByProduct(productId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getUserReviews(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ReviewResponse> directly
        Page<ReviewResponse> response = reviewService.getReviewsByUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable UUID id) {
        // FIX: service returns ReviewResponse directly
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Name", required = false) String userNameHeader) {

        UUID userId = userIdHeader != null ? UUID.fromString(userIdHeader) : null;
        String userName = userNameHeader != null ? userNameHeader : "Anonymous";

        ProductReview review = ProductReview.builder()
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .images(request.getImages())
                .build();

        // FIX: service returns ReviewResponse directly
        ReviewResponse created = reviewService.createReview(review, request.getProductId(), userId, userName);
        return ResponseEntity.ok(ApiResponse.success("Review created successfully", created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }
}