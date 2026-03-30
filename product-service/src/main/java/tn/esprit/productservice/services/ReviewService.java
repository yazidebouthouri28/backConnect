package tn.esprit.productservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.productservice.dto.response.ReviewResponse;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.entities.ProductReview;
import tn.esprit.productservice.exception.DuplicateResourceException;
import tn.esprit.productservice.exception.ResourceNotFoundException;
import tn.esprit.productservice.mapper.ProductMapper;
import tn.esprit.productservice.repositories.ProductRepository;
import tn.esprit.productservice.repositories.ProductReviewRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    // FIX: @Transactional(readOnly=true) keeps session open so mapper can
    // safely access review.product.name without hitting a closed session.
    // Returns Page<ReviewResponse> so the controller never touches raw entities.
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProduct(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(mapper::toReviewResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(mapper::toReviewResponse);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(UUID id) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return mapper.toReviewResponse(review);
    }

    @Transactional
    public ReviewResponse createReview(ProductReview review, UUID productId, UUID userId, String userName) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        reviewRepository.findByProductIdAndUserId(productId, userId).ifPresent(existing -> {
            throw new DuplicateResourceException("User has already reviewed this product");
        });

        review.setProduct(product);
        review.setUserId(userId);
        review.setUserName(userName);

        ProductReview saved = reviewRepository.save(review);
        updateProductRating(product);

        log.info("Review created for product {} by user {}", productId, userId);
        return mapper.toReviewResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(UUID id, ProductReview reviewDetails) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        if (reviewDetails.getRating() != null)  review.setRating(reviewDetails.getRating());
        if (reviewDetails.getTitle() != null)   review.setTitle(reviewDetails.getTitle());
        if (reviewDetails.getComment() != null) review.setComment(reviewDetails.getComment());
        if (reviewDetails.getImages() != null)  review.setImages(reviewDetails.getImages());

        ProductReview saved = reviewRepository.save(review);
        updateProductRating(review.getProduct());
        return mapper.toReviewResponse(saved);
    }

    @Transactional
    public void deleteReview(UUID id) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
        log.info("Review deleted: {}", id);
    }

    private void updateProductRating(Product product) {
        Double avgRating = reviewRepository.getAverageRatingForProduct(product.getId());
        long reviewCount = reviewRepository.countByProductIdAndIsApprovedTrue(product.getId());
        product.setRating(avgRating != null
                ? BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        product.setReviewCount((int) reviewCount);
        productRepository.save(product);
    }
}