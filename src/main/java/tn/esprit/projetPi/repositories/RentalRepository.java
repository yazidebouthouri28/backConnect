package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Rental;
import tn.esprit.projetPi.entities.RentalStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends MongoRepository<Rental, String> {
    
    List<Rental> findByUserId(String userId);
    
    List<Rental> findByProductId(String productId);
    
    List<Rental> findByStatus(RentalStatus status);
    
    List<Rental> findByUserIdAndStatus(String userId, RentalStatus status);
    
    @Query("{'endDate': {$lt: ?0}, 'status': 'ACTIVE'}")
    List<Rental> findOverdueRentals(LocalDateTime now);
    
    @Query("{'endDate': {$gte: ?0, $lte: ?1}, 'status': 'ACTIVE'}")
    List<Rental> findRentalsEndingSoon(LocalDateTime start, LocalDateTime end);
}
