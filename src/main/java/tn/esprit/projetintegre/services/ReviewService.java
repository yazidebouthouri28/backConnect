package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.dto.request.ReviewRequest;
import tn.esprit.projetintegre.dto.response.ReviewResponse;
import tn.esprit.projetintegre.entities.Review;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.ReviewRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SiteRepository siteRepository;
    private final SiteModuleMapper siteMapper;

    public List<ReviewResponse> getReviewsBySite(Long siteId) {
        return siteMapper.toReviewResponseList(reviewRepository.findBySite_Id(siteId));
    }

    public ReviewResponse addReview(Long siteId, ReviewRequest request) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        Review review = siteMapper.toEntity(request, site);
        Review saved = reviewRepository.save(review);
        updateAverageRating(site);
        return siteMapper.toResponse(saved);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        siteMapper.updateEntity(existing, request);
        Review saved = reviewRepository.save(existing);
        updateAverageRating(existing.getSite());
        return siteMapper.toResponse(saved);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Site site = review.getSite();
        reviewRepository.deleteById(reviewId);
        updateAverageRating(site);
    }

    private void updateAverageRating(Site site) {
        // Wait: The original code had site.getSiteId(), which is wrong.
        // Site id getter is getId()
        Long siteId = site.getId();
        List<Review> reviews = reviewRepository.findBySite_Id(siteId);
        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        site.setAverageRating(java.math.BigDecimal.valueOf(avg));
        siteRepository.save(site);
    }
}