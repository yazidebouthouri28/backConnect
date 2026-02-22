package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Event;
import tn.esprit.projetPi.entities.EventStatus;
import tn.esprit.projetPi.entities.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    List<Event> findByIsPublishedTrue();
    
    List<Event> findByIsFeaturedTrue();
    
    List<Event> findByOrganizerId(String organizerId);
    
    List<Event> findByStatus(EventStatus status);
    
    List<Event> findByType(EventType type);
    
    List<Event> findByCity(String city);
    
    List<Event> findByStartDateAfter(LocalDateTime date);
    
    List<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Event> searchByTitle(String title);
    
    @Query("{'$or': [{'title': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}, {'city': {$regex: ?0, $options: 'i'}}]}")
    List<Event> searchEvents(String query);
    
    List<Event> findByIsFreeTrue();
    
    List<Event> findByPriceLessThanEqual(Double maxPrice);
    
    @Query("{'tags': {$in: ?0}}")
    List<Event> findByTagsIn(List<String> tags);
    
    @Query("{'categories': {$in: ?0}}")
    List<Event> findByCategoriesIn(List<String> categories);
    
    @Query("{'sponsorIds': ?0}")
    List<Event> findBySponsorId(String sponsorId);
    
    List<Event> findByIsVirtualTrue();
    
    @Query(value = "{'startDate': {$gte: ?0}, 'isPublished': true, 'status': {$in: ['PUBLISHED', 'REGISTRATION_OPEN']}}", sort = "{'startDate': 1}")
    List<Event> findUpcomingEvents(LocalDateTime now);
}
