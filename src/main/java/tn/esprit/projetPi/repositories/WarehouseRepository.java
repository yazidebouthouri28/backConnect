package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Warehouse;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    
    List<Warehouse> findByIsActiveTrue();
    
    Optional<Warehouse> findByCode(String code);
    
    List<Warehouse> findByCity(String city);
    
    List<Warehouse> findByCountry(String country);
    
    boolean existsByCode(String code);
}
