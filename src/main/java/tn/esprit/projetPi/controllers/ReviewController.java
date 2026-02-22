package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.CreateReviewRequest;
import tn.esprit.projetPi.dto.ReviewDTO;
import tn.esprit.projetPi.entities.ReviewTargetType;
import tn.esprit.projetPi.services.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReviewById(@PathVariable String id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review retrieved successfully", review));
    }

    @GetMapping("/target/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByTarget(
            @PathVariable ReviewTargetType targetType,
            @PathVariable String targetId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByTarget(targetId, targetType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
    }

    @GetMapping("/target/{targetType}/{targetId}/paged")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getReviewsByTargetPaged(
            @PathVariable ReviewTargetType targetType,
            @PathVariable String targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDTO> reviews = reviewService.getReviewsByTarget(targetId, targetType, page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
    }

    @GetMapping("/campsite/{campsiteId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getCampsiteReviews(@PathVariable String campsiteId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByTarget(campsiteId, ReviewTargetType.CAMPSITE);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite reviews retrieved successfully", reviews));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getEventReviews(@PathVariable String eventId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByTarget(eventId, ReviewTargetType.EVENT);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event reviews retrieved successfully", reviews));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Your reviews retrieved successfully", reviews));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getPendingReviews() {
        List<ReviewDTO> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending reviews retrieved successfully", reviews));
    }

    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReportedReviews() {
        List<ReviewDTO> reviews = reviewService.getReportedReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reported reviews retrieved successfully", reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDTO created = reviewService.createReview(request, userDetails.getUsername(), userDetails.getUsername(), null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Review created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(
            @PathVariable String id,
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDTO updated = reviewService.updateReview(id, request, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Review updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Review deleted successfully", null));
    }

    @PostMapping("/{id}/response")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> addOwnerResponse(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDTO review = reviewService.addOwnerResponse(id, body.get("response"), userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Response added successfully", review));
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<ApiResponse<ReviewDTO>> markHelpful(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDTO review = reviewService.markHelpful(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Review marked as helpful", review));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse<ReviewDTO>> reportReview(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        ReviewDTO review = reviewService.reportReview(id, body.get("reason"));
        return ResponseEntity.ok(new ApiResponse<>(true, "Review reported successfully", review));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> approveReview(@PathVariable String id) {
        ReviewDTO review = reviewService.approveReview(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review approved successfully", review));
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> hideReview(@PathVariable String id) {
        ReviewDTO review = reviewService.hideReview(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review hidden successfully", review));
    }
}
