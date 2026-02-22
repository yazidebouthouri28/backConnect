package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Product;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findBySellerId(String sellerId);
    
    Page<Product> findBySellerId(String sellerId, Pageable pageable);
    
    List<Product> findByCategoryId(String categoryId);
    
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);
    
    List<Product> findByIsActive(Boolean isActive);
    
    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);
    
    List<Product> findByIsApproved(Boolean isApproved);
    
    Page<Product> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    List<Product> findByIsFeatured(Boolean isFeatured);
    
    List<Product> findByInStock(Boolean inStock);
    
    List<Product> findByRentalAvailable(Boolean rentalAvailable);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchByName(String name);
    
    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    List<Product> searchByKeyword(String keyword);
    
    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    Page<Product> searchByKeyword(String keyword, Pageable pageable);
    
    List<Product> findByTagsContaining(String tag);
    
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("{ 'stockQuantity': { $lte: ?0 } }")
    List<Product> findLowStockProducts(Integer threshold);
    
    @Query("{ 'availableStock': { $lte: 0 } }")
    List<Product> findOutOfStockProducts();
    
    @Query("{ 'sellerId': ?0, 'availableStock': { $lte: 0 } }")
    List<Product> findOutOfStockBySelerId(String sellerId);
    
    @Query("{ 'averageRating': { $gte: ?0 } }")
    List<Product> findByMinRating(Double minRating);
    
    long countBySellerId(String sellerId);
    
    long countByIsActive(Boolean isActive);
    
    long countByIsApproved(Boolean isApproved);
    
    long countByCategoryId(String categoryId);
    
    @Query(value = "{ 'sellerId': ?0, 'isActive': true }", count = true)
    long countActiveProductsBySeller(String sellerId);
    
    List<Product> findTop10ByOrderByPurchaseCountDesc();
    
    List<Product> findTop10ByOrderByAverageRatingDesc();
    
    List<Product> findBySellerIdAndIsActive(String sellerId, Boolean isActive);
    
    Page<Product> findBySellerIdAndIsActive(String sellerId, Boolean isActive, Pageable pageable);
}
