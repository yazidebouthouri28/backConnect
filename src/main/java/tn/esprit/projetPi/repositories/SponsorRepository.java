package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Sponsor;
import tn.esprit.projetPi.entities.SponsorTier;

import java.util.List;

@Repository
public interface SponsorRepository extends MongoRepository<Sponsor, String> {
    
    List<Sponsor> findByIsActiveTrue();
    
    List<Sponsor> findByTier(SponsorTier tier);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Sponsor> searchByName(String name);
    
    @Query("{'sponsoredEventIds': ?0}")
    List<Sponsor> findByEventId(String eventId);
    
    List<Sponsor> findByIsActiveTrueOrderByTierAsc();
}
