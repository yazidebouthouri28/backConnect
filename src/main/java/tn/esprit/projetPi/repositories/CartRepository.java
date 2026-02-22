package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Cart;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    
    Optional<Cart> findByUserId(String userId);
    
    void deleteByUserId(String userId);
}
