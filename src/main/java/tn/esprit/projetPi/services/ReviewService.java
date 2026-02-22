package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CreateReviewRequest;
import tn.esprit.projetPi.dto.ReviewDTO;
import tn.esprit.projetPi.entities.Review;
import tn.esprit.projetPi.entities.ReviewTargetType;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.ReviewRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CampsiteService campsiteService;
    private final EventService eventService;

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO getReviewById(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return toDTO(review);
    }

    public List<ReviewDTO> getReviewsByTarget(String targetId, ReviewTargetType targetType) {
        return reviewRepository.findApprovedReviews(targetId, targetType).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ReviewDTO> getReviewsByTarget(String targetId, ReviewTargetType targetType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewRepository.findByTargetIdAndTargetType(targetId, targetType, pageable)
                .map(this::toDTO);
    }

    public List<ReviewDTO> getReviewsByUser(String userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO createReview(CreateReviewRequest request, String userId, String userName, String userAvatar) {
        // Check if user already reviewed this target
        if (reviewRepository.findByUserIdAndTargetIdAndTargetType(userId, request.getTargetId(), request.getTargetType()).isPresent()) {
            throw new IllegalStateException("User has already reviewed this item");
        }
        
        Review review = new Review();
        review.setUserId(userId);
        review.setUserName(userName);
        review.setUserAvatar(userAvatar);
        review.setTargetType(request.getTargetType());
        review.setTargetId(request.getTargetId());
        review.setReservationId(request.getReservationId());
        review.setRating(request.getRating());
        review.setCleanlinessRating(request.getCleanlinessRating());
        review.setLocationRating(request.getLocationRating());
        review.setValueRating(request.getValueRating());
        review.setAmenitiesRating(request.getAmenitiesRating());
        review.setServiceRating(request.getServiceRating());
        review.setAccuracyRating(request.getAccuracyRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setPros(request.getPros());
        review.setCons(request.getCons());
        review.setIsVerified(request.getReservationId() != null);
        review.setIsApproved(true); // Auto-approve for now
        review.setIsHidden(false);
        review.setIsReported(false);
        review.setHelpfulCount(0);
        review.setHelpfulByUserIds(new ArrayList<>());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        // Calculate overall score
        double overallScore = calculateOverallScore(review);
        review.setOverallScore(overallScore);
        
        Review savedReview = reviewRepository.save(review);
        
        // Update target's average rating
        updateTargetRating(request.getTargetId(), request.getTargetType());
        
        return toDTO(savedReview);
    }

    public ReviewDTO updateReview(String id, CreateReviewRequest request, String userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        if (!userId.equals(review.getUserId())) {
            throw new IllegalStateException("Cannot update another user's review");
        }
        
        review.setRating(request.getRating());
        review.setCleanlinessRating(request.getCleanlinessRating());
        review.setLocationRating(request.getLocationRating());
        review.setValueRating(request.getValueRating());
        review.setAmenitiesRating(request.getAmenitiesRating());
        review.setServiceRating(request.getServiceRating());
        review.setAccuracyRating(request.getAccuracyRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setPros(request.getPros());
        review.setCons(request.getCons());
        review.setOverallScore(calculateOverallScore(review));
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update target's average rating
        updateTargetRating(review.getTargetId(), review.getTargetType());
        
        return toDTO(savedReview);
    }

    public void deleteReview(String id, String userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        if (!userId.equals(review.getUserId())) {
            throw new IllegalStateException("Cannot delete another user's review");
        }
        
        String targetId = review.getTargetId();
        ReviewTargetType targetType = review.getTargetType();
        
        reviewRepository.deleteById(id);
        
        // Update target's average rating
        updateTargetRating(targetId, targetType);
    }

    public ReviewDTO addOwnerResponse(String reviewId, String response, String ownerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        review.setOwnerResponse(response);
        review.setOwnerResponseAt(LocalDateTime.now());
        review.setOwnerId(ownerId);
        review.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reviewRepository.save(review));
    }

    public ReviewDTO markHelpful(String reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (review.getHelpfulByUserIds() == null) {
            review.setHelpfulByUserIds(new ArrayList<>());
        }
        
        if (!review.getHelpfulByUserIds().contains(userId)) {
            review.getHelpfulByUserIds().add(userId);
            review.setHelpfulCount(review.getHelpfulByUserIds().size());
            reviewRepository.save(review);
        }
        
        return toDTO(review);
    }

    public ReviewDTO reportReview(String reviewId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        review.setIsReported(true);
        review.setReportReason(reason);
        review.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reviewRepository.save(review));
    }

    public ReviewDTO approveReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        review.setIsApproved(true);
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        updateTargetRating(review.getTargetId(), review.getTargetType());
        
        return toDTO(savedReview);
    }

    public ReviewDTO hideReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        review.setIsHidden(true);
        review.setUpdatedAt(LocalDateTime.now());
        return toDTO(reviewRepository.save(review));
    }

    public List<ReviewDTO> getPendingReviews() {
        return reviewRepository.findByIsApprovedFalse().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReportedReviews() {
        return reviewRepository.findByIsReportedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private double calculateOverallScore(Review review) {
        int count = 0;
        double sum = 0;
        
        if (review.getRating() != null) { sum += review.getRating(); count++; }
        if (review.getCleanlinessRating() != null) { sum += review.getCleanlinessRating(); count++; }
        if (review.getLocationRating() != null) { sum += review.getLocationRating(); count++; }
        if (review.getValueRating() != null) { sum += review.getValueRating(); count++; }
        if (review.getAmenitiesRating() != null) { sum += review.getAmenitiesRating(); count++; }
        if (review.getServiceRating() != null) { sum += review.getServiceRating(); count++; }
        if (review.getAccuracyRating() != null) { sum += review.getAccuracyRating(); count++; }
        
        return count > 0 ? sum / count : 0;
    }

    private void updateTargetRating(String targetId, ReviewTargetType targetType) {
        List<Review> reviews = reviewRepository.findApprovedReviews(targetId, targetType);
        
        if (reviews.isEmpty()) {
            return;
        }
        
        double avgRating = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
        
        int reviewCount = reviews.size();
        
        if (targetType == ReviewTargetType.CAMPSITE) {
            campsiteService.updateRating(targetId, avgRating, reviewCount);
        } else if (targetType == ReviewTargetType.EVENT) {
            eventService.updateRating(targetId, avgRating, reviewCount);
        }
    }

    private ReviewDTO toDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .targetType(review.getTargetType())
                .targetId(review.getTargetId())
                .targetName(review.getTargetName())
                .reservationId(review.getReservationId())
                .rating(review.getRating())
                .overallScore(review.getOverallScore())
                .cleanlinessRating(review.getCleanlinessRating())
                .locationRating(review.getLocationRating())
                .valueRating(review.getValueRating())
                .amenitiesRating(review.getAmenitiesRating())
                .serviceRating(review.getServiceRating())
                .accuracyRating(review.getAccuracyRating())
                .title(review.getTitle())
                .content(review.getContent())
                .images(review.getImages())
                .pros(review.getPros())
                .cons(review.getCons())
                .ownerResponse(review.getOwnerResponse())
                .ownerResponseAt(review.getOwnerResponseAt())
                .ownerId(review.getOwnerId())
                .isVerified(review.getIsVerified())
                .isApproved(review.getIsApproved())
                .isHidden(review.getIsHidden())
                .helpfulCount(review.getHelpfulCount())
                .isReported(review.getIsReported())
                .reportReason(review.getReportReason())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
