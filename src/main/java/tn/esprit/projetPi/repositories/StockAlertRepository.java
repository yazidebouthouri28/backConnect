package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.AlertType;
import tn.esprit.projetPi.entities.StockAlert;

import java.util.List;

@Repository
public interface StockAlertRepository extends MongoRepository<StockAlert, String> {
    
    List<StockAlert> findByProductId(String productId);
    
    List<StockAlert> findBySellerId(String sellerId);
    
    Page<StockAlert> findBySellerId(String sellerId, Pageable pageable);
    
    List<StockAlert> findByAcknowledged(Boolean acknowledged);
    
    Page<StockAlert> findByAcknowledged(Boolean acknowledged, Pageable pageable);
    
    List<StockAlert> findBySellerIdAndAcknowledged(String sellerId, Boolean acknowledged);
    
    List<StockAlert> findByType(AlertType type);
    
    long countBySellerIdAndAcknowledged(String sellerId, Boolean acknowledged);
}
