package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.CouponUsage;

import java.util.List;

@Repository
public interface CouponUsageRepository extends MongoRepository<CouponUsage, String> {
    
    List<CouponUsage> findByCouponId(String couponId);
    
    List<CouponUsage> findByUserId(String userId);
    
    List<CouponUsage> findByCouponIdAndUserId(String couponId, String userId);
    
    long countByCouponId(String couponId);
    
    long countByCouponIdAndUserId(String couponId, String userId);
    
    boolean existsByCouponIdAndOrderId(String couponId, String orderId);
}
