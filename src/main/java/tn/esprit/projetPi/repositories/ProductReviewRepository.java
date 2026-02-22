package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.ProductReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    
    List<ProductReview> findByProductId(String productId);
    
    Page<ProductReview> findByProductId(String productId, Pageable pageable);
    
    List<ProductReview> findByUserId(String userId);
    
    Page<ProductReview> findByUserId(String userId, Pageable pageable);
    
    List<ProductReview> findByProductIdAndApproved(String productId, Boolean approved);
    
    Page<ProductReview> findByProductIdAndApproved(String productId, Boolean approved, Pageable pageable);
    
    List<ProductReview> findByApproved(Boolean approved);
    
    Page<ProductReview> findByApproved(Boolean approved, Pageable pageable);
    
    Optional<ProductReview> findByUserIdAndProductId(String userId, String productId);
    
    Optional<ProductReview> findByUserIdAndOrderId(String userId, String orderId);
    
    boolean existsByUserIdAndProductId(String userId, String productId);
    
    boolean existsByUserIdAndOrderIdAndProductId(String userId, String orderId, String productId);
    
    @Query(value = "{ 'productId': ?0 }", count = true)
    long countByProductId(String productId);
    
    @Query(value = "{ 'productId': ?0, 'approved': true }", fields = "{ 'rating': 1 }")
    List<ProductReview> findRatingsByProductId(String productId);
    
    List<ProductReview> findByFeatured(Boolean featured);
    
    List<ProductReview> findTop10ByProductIdOrderByHelpfulCountDesc(String productId);
}
