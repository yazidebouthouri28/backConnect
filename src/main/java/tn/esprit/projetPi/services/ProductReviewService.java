package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Product;
import tn.esprit.projetPi.entities.ProductReview;
import tn.esprit.projetPi.entities.User;
import tn.esprit.projetPi.exception.DuplicateResourceException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.OrderRepository;
import tn.esprit.projetPi.repositories.ProductRepository;
import tn.esprit.projetPi.repositories.ProductReviewRepository;
import tn.esprit.projetPi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public PageResponse<ProductReviewDTO> getReviewsByProduct(String productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReview> reviewPage = reviewRepository.findByProductIdAndApproved(productId, true, pageable);
        return toPageResponse(reviewPage);
    }

    public PageResponse<ProductReviewDTO> getReviewsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReview> reviewPage = reviewRepository.findByUserId(userId, pageable);
        return toPageResponse(reviewPage);
    }

    public PageResponse<ProductReviewDTO> getPendingReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ProductReview> reviewPage = reviewRepository.findByApproved(false, pageable);
        return toPageResponse(reviewPage);
    }

    public ProductReviewDTO getReviewById(String id) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return toDTO(review);
    }

    public ProductReviewDTO createReview(String userId, CreateReviewRequest request) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        // Check if user has purchased this product (verified purchase)
        boolean hasPurchased = !orderRepository.findDeliveredOrdersWithProduct(userId, request.getProductId()).isEmpty();

        ProductReview review = new ProductReview();
        review.setProductId(request.getProductId());
        review.setProductName(product.getName());
        review.setUserId(userId);
        review.setUserName(user.getName());
        review.setUserAvatar(user.getAvatar());
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setImages(request.getImages());
        review.setVerified(hasPurchased);
        review.setApproved(false); // Requires admin approval
        review.setFeatured(false);
        review.setHelpfulCount(0);
        review.setHelpfulVotes(new ArrayList<>());
        review.setCreatedAt(LocalDateTime.now());

        ProductReview saved = reviewRepository.save(review);

        // Update product rating
        updateProductRating(request.getProductId());

        log.info("Review created for product {} by user {}", request.getProductId(), userId);
        return toDTO(saved);
    }

    public ProductReviewDTO updateReview(String reviewId, String userId, CreateReviewRequest request) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getTitle() != null) review.setTitle(request.getTitle());
        if (request.getComment() != null) review.setComment(request.getComment());
        if (request.getImages() != null) review.setImages(request.getImages());
        review.setApproved(false); // Reset approval on update
        review.setUpdatedAt(LocalDateTime.now());

        ProductReview saved = reviewRepository.save(review);
        updateProductRating(review.getProductId());

        return toDTO(saved);
    }

    public ProductReviewDTO approveReview(String reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setApproved(true);
        review.setUpdatedAt(LocalDateTime.now());
        ProductReview saved = reviewRepository.save(review);

        updateProductRating(review.getProductId());

        return toDTO(saved);
    }

    public ProductReviewDTO rejectReview(String reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setApproved(false);
        review.setUpdatedAt(LocalDateTime.now());

        return toDTO(reviewRepository.save(review));
    }

    public ProductReviewDTO toggleFeatured(String reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setFeatured(!Boolean.TRUE.equals(review.getFeatured()));
        review.setUpdatedAt(LocalDateTime.now());

        return toDTO(reviewRepository.save(review));
    }

    public ProductReviewDTO addSellerResponse(String reviewId, String sellerId, String response) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Verify seller owns the product
        Product product = productRepository.findById(review.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("Only the product seller can respond to reviews");
        }

        review.setSellerResponse(response);
        review.setSellerResponseAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        return toDTO(reviewRepository.save(review));
    }

    public ProductReviewDTO markHelpful(String reviewId, String userId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (review.getHelpfulVotes() == null) {
            review.setHelpfulVotes(new ArrayList<>());
        }

        if (review.getHelpfulVotes().contains(userId)) {
            // Remove vote
            review.getHelpfulVotes().remove(userId);
            review.setHelpfulCount(review.getHelpfulCount() - 1);
        } else {
            // Add vote
            review.getHelpfulVotes().add(userId);
            review.setHelpfulCount((review.getHelpfulCount() != null ? review.getHelpfulCount() : 0) + 1);
        }

        return toDTO(reviewRepository.save(review));
    }

    public void deleteReview(String reviewId, String userId, boolean isAdmin) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        String productId = review.getProductId();
        reviewRepository.deleteById(reviewId);
        updateProductRating(productId);
    }

    public List<ProductReviewDTO> getFeaturedReviews() {
        return reviewRepository.findByFeatured(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductReviewDTO> getTopReviewsForProduct(String productId) {
        return reviewRepository.findTop10ByProductIdOrderByHelpfulCountDesc(productId).stream()
                .filter(r -> Boolean.TRUE.equals(r.getApproved()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private void updateProductRating(String productId) {
        List<ProductReview> approvedReviews = reviewRepository.findByProductIdAndApproved(productId, true);
        
        if (approvedReviews.isEmpty()) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                product.setAverageRating(0.0);
                product.setReviewCount(0);
                productRepository.save(product);
            }
            return;
        }

        double average = approvedReviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setAverageRating(Math.round(average * 10.0) / 10.0); // Round to 1 decimal
            product.setReviewCount(approvedReviews.size());
            productRepository.save(product);
        }
    }

    private ProductReviewDTO toDTO(ProductReview review) {
        return ProductReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .productName(review.getProductName())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .orderId(review.getOrderId())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .images(review.getImages())
                .verified(review.getVerified())
                .approved(review.getApproved())
                .featured(review.getFeatured())
                .helpfulCount(review.getHelpfulCount())
                .sellerResponse(review.getSellerResponse())
                .sellerResponseAt(review.getSellerResponseAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private PageResponse<ProductReviewDTO> toPageResponse(Page<ProductReview> page) {
        return PageResponse.<ProductReviewDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
