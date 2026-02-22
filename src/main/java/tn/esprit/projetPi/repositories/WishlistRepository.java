package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Wishlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    
    List<Wishlist> findByUserId(String userId);
    
    Optional<Wishlist> findByUserIdAndIsDefault(String userId, Boolean isDefault);
    
    Optional<Wishlist> findByUserIdAndName(String userId, String name);
    
    boolean existsByUserIdAndName(String userId, String name);
    
    List<Wishlist> findByIsPublic(Boolean isPublic);
}
