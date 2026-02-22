package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Order;
import tn.esprit.projetPi.entities.OrderStatus;
import tn.esprit.projetPi.entities.OrderType;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    List<Order> findByUserId(String userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByType(OrderType type);
    
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    
    @Query("{'orderDate': {$gte: ?0, $lte: ?1}}")
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByTrackingNumber(String trackingNumber);
}
