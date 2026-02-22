package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Inventory;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    
    List<Inventory> findByProductId(String productId);
    
    List<Inventory> findByWarehouseId(String warehouseId);
    
    Optional<Inventory> findByProductIdAndWarehouseId(String productId, String warehouseId);
    
    List<Inventory> findByIsLowStockTrue();
    
    @Query("{'currentStock': {$lte: '$lowStockThreshold'}}")
    List<Inventory> findLowStockItems();
    
    List<Inventory> findBySku(String sku);
}
