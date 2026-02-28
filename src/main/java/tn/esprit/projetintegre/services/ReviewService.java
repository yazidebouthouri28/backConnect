package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Review;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.repositories.ReviewRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SiteRepository siteRepository;

    public List<Review> getReviewsBySite(Long siteId) {
        return reviewRepository.findBySite_SiteId(siteId);
    }

    public Review addReview(Long siteId, Review review) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        review.setSite(site);
        Review saved = reviewRepository.save(review);
        updateAverageRating(site);
        return saved;
    }

    public Review updateReview(Long reviewId, Review updated) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        existing.setRating(updated.getRating());
        existing.setComment(updated.getComment());
        Review saved = reviewRepository.save(existing);
        updateAverageRating(existing.getSite());
        return saved;
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Site site = review.getSite();
        reviewRepository.deleteById(reviewId);
        updateAverageRating(site);
    }

    private void updateAverageRating(Site site) {
        List<Review> reviews = reviewRepository.findBySite_SiteId(site.getSiteId());
        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        site.setAverageRating(avg);
        siteRepository.save(site);
    }
}