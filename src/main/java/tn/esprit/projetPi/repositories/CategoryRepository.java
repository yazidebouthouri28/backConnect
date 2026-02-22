package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    
    Optional<Category> findBySlug(String slug);
    
    Optional<Category> findByName(String name);
    
    List<Category> findByParentId(String parentId);
    
    List<Category> findByParentIdIsNull();
    
    List<Category> findByLevel(Integer level);
    
    List<Category> findByIsActive(Boolean isActive);
    
    List<Category> findByIsFeatured(Boolean isFeatured);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Category> searchByName(String name);
    
    boolean existsBySlug(String slug);
    
    boolean existsByNameIgnoreCase(String name);
    
    List<Category> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    List<Category> findByParentIdAndIsActiveOrderBySortOrderAsc(String parentId, Boolean isActive);
}
