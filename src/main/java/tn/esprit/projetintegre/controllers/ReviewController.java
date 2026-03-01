package tn.esprit.projetintegre.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.request.ReviewRequest;
import tn.esprit.projetintegre.dto.response.ReviewResponse;
import tn.esprit.projetintegre.services.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/site/{siteId}")
    public List<ReviewResponse> getReviewsBySite(@PathVariable Long siteId) {
        return reviewService.getReviewsBySite(siteId);
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long siteId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(siteId, request));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}