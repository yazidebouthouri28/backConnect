package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.RefundRequest;
import tn.esprit.projetPi.entities.RefundStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRequestRepository extends MongoRepository<RefundRequest, String> {
    
    List<RefundRequest> findByUserId(String userId);
    
    Page<RefundRequest> findByUserId(String userId, Pageable pageable);
    
    List<RefundRequest> findByOrderId(String orderId);
    
    List<RefundRequest> findBySellerId(String sellerId);
    
    Page<RefundRequest> findBySellerId(String sellerId, Pageable pageable);
    
    List<RefundRequest> findByStatus(RefundStatus status);
    
    Page<RefundRequest> findByStatus(RefundStatus status, Pageable pageable);
    
    Optional<RefundRequest> findByOrderIdAndUserId(String orderId, String userId);
    
    long countByStatus(RefundStatus status);
    
    long countBySellerId(String sellerId);
    
    long countBySellerIdAndStatus(String sellerId, RefundStatus status);
}
