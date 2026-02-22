package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Campsite;

import java.util.List;

@Repository
public interface CampsiteRepository extends MongoRepository<Campsite, String> {
    
    List<Campsite> findByIsActiveTrue();
    
    List<Campsite> findByIsFeaturedTrue();
    
    List<Campsite> findByOwnerId(String ownerId);
    
    List<Campsite> findByCity(String city);
    
    List<Campsite> findByState(String state);
    
    List<Campsite> findByCountry(String country);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Campsite> searchByName(String name);
    
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}, {'city': {$regex: ?0, $options: 'i'}}]}")
    List<Campsite> searchCampsites(String query);
    
    List<Campsite> findByPricePerNightBetween(Double minPrice, Double maxPrice);
    
    List<Campsite> findByMaxGuestsGreaterThanEqual(Integer guests);
    
    @Query("{'amenities': {$all: ?0}}")
    List<Campsite> findByAmenitiesContainingAll(List<String> amenities);
    
    @Query("{'amenities': {$in: ?0}}")
    List<Campsite> findByAmenitiesContainingAny(List<String> amenities);
    
    List<Campsite> findByAverageRatingGreaterThanEqual(Double rating);
    
    @Query(value = "{'latitude': {$gte: ?0, $lte: ?1}, 'longitude': {$gte: ?2, $lte: ?3}, 'isActive': true}")
    List<Campsite> findByLocationBounds(Double minLat, Double maxLat, Double minLng, Double maxLng);
}
