package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Review;
import tn.esprit.projetPi.entities.ReviewTargetType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    List<Review> findByTargetIdAndTargetType(String targetId, ReviewTargetType targetType);
    
    Page<Review> findByTargetIdAndTargetType(String targetId, ReviewTargetType targetType, Pageable pageable);
    
    List<Review> findByUserId(String userId);
    
    List<Review> findByTargetIdAndIsApprovedTrue(String targetId);
    
    Optional<Review> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, ReviewTargetType targetType);
    
    Optional<Review> findByReservationId(String reservationId);
    
    @Query(value = "{'targetId': ?0, 'targetType': ?1, 'isApproved': true}", count = true)
    Long countApprovedReviews(String targetId, ReviewTargetType targetType);
    
    @Query(value = "{'targetId': ?0, 'targetType': ?1, 'isApproved': true}")
    List<Review> findApprovedReviews(String targetId, ReviewTargetType targetType);
    
    List<Review> findByIsApprovedFalse();
    
    List<Review> findByIsReportedTrue();
    
    @Query("{'rating': ?0, 'targetType': ?1}")
    List<Review> findByRatingAndTargetType(Integer rating, ReviewTargetType targetType);
}
