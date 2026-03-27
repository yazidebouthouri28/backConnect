package tn.esprit.productservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.productservice.entities.ProductReview;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {

    Page<ProductReview> findByProductId(UUID productId, Pageable pageable);

    Page<ProductReview> findByUserId(UUID userId, Pageable pageable);

    Optional<ProductReview> findByProductIdAndUserId(UUID productId, UUID userId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId AND r.isApproved = true")
    Double getAverageRatingForProduct(UUID productId);

    long countByProductIdAndIsApprovedTrue(UUID productId);
}
