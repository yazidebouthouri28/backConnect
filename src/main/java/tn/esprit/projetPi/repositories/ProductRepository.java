package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Product;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByIsActiveTrue();
    
    List<Product> findByIsFeaturedTrue();
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("{'tags': ?0}")
    List<Product> findByTag(String tag);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByRentalAvailableTrue();
    
    List<Product> findByInStockTrue();
    
    @Query("{'stockQuantity': {$lte: ?0}}")
    List<Product> findLowStockProducts(Integer threshold);
    
    List<Product> findBySku(String sku);
}
