package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.MovementType;
import tn.esprit.projetPi.entities.StockMovement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
    
    List<StockMovement> findByInventoryId(String inventoryId);
    
    List<StockMovement> findByProductId(String productId);
    
    List<StockMovement> findByWarehouseId(String warehouseId);
    
    List<StockMovement> findByType(MovementType type);
    
    List<StockMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
