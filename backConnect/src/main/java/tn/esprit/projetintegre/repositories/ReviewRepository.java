package tn.esprit.projetintegre.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.projetintegre.entities.Review;
import tn.esprit.projetintegre.enums.ReviewTargetType;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"site", "user"})
    List<Review> findBySite_Id(Long siteId);

    Page<Review> findByTargetTypeAndTargetId(ReviewTargetType targetType, Long targetId, Pageable pageable);

    Optional<Review> findByUserIdAndTargetTypeAndTargetId(Long userId, ReviewTargetType targetType, Long targetId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId AND r.approved = true")
    Double getAverageRating(@Param("targetType") ReviewTargetType targetType, @Param("targetId") Long targetId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId AND r.approved = true")
    Long countApprovedReviews(@Param("targetType") ReviewTargetType targetType, @Param("targetId") Long targetId);
}
