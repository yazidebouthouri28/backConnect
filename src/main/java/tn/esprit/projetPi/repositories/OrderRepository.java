package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Order;
import tn.esprit.projetPi.entities.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    List<Order> findByUserId(String userId);
    
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    List<Order> findBySellerId(String sellerId);
    
    Page<Order> findBySellerId(String sellerId, Pageable pageable);
    
    List<Order> findByStatus(OrderStatus status);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    
    List<Order> findBySellerIdAndStatus(String sellerId, OrderStatus status);
    
    @Query("{ 'orderDate': { $gte: ?0, $lte: ?1 } }")
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'userId': ?0, 'items.productId': ?1, 'status': { $in: ['DELIVERED', 'COMPLETED'] } }")
    List<Order> findDeliveredOrdersWithProduct(String userId, String productId);
    
    long countByStatus(OrderStatus status);
    
    long countBySellerId(String sellerId);
    
    long countBySellerIdAndStatus(String sellerId, OrderStatus status);
    
    @Query(value = "{ 'orderDate': { $gte: ?0 } }", count = true)
    long countOrdersAfter(LocalDateTime date);
    
    @Query(value = "{ 'sellerId': ?0, 'orderDate': { $gte: ?1, $lte: ?2 } }")
    List<Order> findBySellerIdAndDateRange(String sellerId, LocalDateTime start, LocalDateTime end);
}
