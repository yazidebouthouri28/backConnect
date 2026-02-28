package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.Review;
import tn.esprit.projetintegre.services.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/site/{siteId}")
    public List<Review> getReviewsBySite(@PathVariable Long siteId) {
        return reviewService.getReviewsBySite(siteId);
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<Review> addReview(@PathVariable Long siteId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(siteId, review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}