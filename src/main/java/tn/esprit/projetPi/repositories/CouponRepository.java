package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {
    
    Optional<Coupon> findByCode(String code);
    
    Optional<Coupon> findByCodeIgnoreCase(String code);
    
    boolean existsByCode(String code);
    
    boolean existsByCodeIgnoreCase(String code);
    
    List<Coupon> findByIsActive(Boolean isActive);
    
    Page<Coupon> findByIsActive(Boolean isActive, Pageable pageable);
    
    List<Coupon> findByIsActiveAndIsPublic(Boolean isActive, Boolean isPublic);
    
    @Query("{ 'isActive': true, 'validFrom': { $lte: ?0 }, 'validUntil': { $gte: ?0 } }")
    List<Coupon> findValidCoupons(LocalDateTime now);
    
    @Query("{ 'isActive': true, 'isPublic': true, 'validFrom': { $lte: ?0 }, 'validUntil': { $gte: ?0 } }")
    List<Coupon> findValidPublicCoupons(LocalDateTime now);
    
    List<Coupon> findByCreatedBy(String userId);
    
    @Query("{ 'validUntil': { $lt: ?0 } }")
    List<Coupon> findExpiredCoupons(LocalDateTime now);
}
