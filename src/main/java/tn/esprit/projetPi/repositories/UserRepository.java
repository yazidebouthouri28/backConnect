package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Role;
import tn.esprit.projetPi.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    Page<User> findByRole(Role role, Pageable pageable);
    
    List<User> findByIsSeller(Boolean isSeller);
    
    Page<User> findByIsSeller(Boolean isSeller, Pageable pageable);
    
    List<User> findByIsActive(Boolean isActive);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    List<User> findByIsSuspended(Boolean isSuspended);
    
    Page<User> findByIsSuspended(Boolean isSuspended, Pageable pageable);
    
    @Query("{ 'username': { $regex: ?0, $options: 'i' } }")
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<User> findByEmailContainingIgnoreCase(String email);
    
    @Query("{ '$or': [ { 'username': { $regex: ?0, $options: 'i' } }, { 'name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    List<User> searchUsers(String query);
    
    @Query("{ '$or': [ { 'username': { $regex: ?0, $options: 'i' } }, { 'name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchUsers(String query, Pageable pageable);
    
    @Query(value = "{ 'createdAt': { $gte: ?0 } }", count = true)
    long countNewUsersAfter(LocalDateTime date);
    
    @Query(value = "{ 'lastLoginAt': { $gte: ?0 } }", count = true)
    long countActiveUsersAfter(LocalDateTime date);
    
    long countByRole(Role role);
    
    long countByIsSeller(Boolean isSeller);
    
    long countByIsActive(Boolean isActive);
    
    long countByIsSuspended(Boolean isSuspended);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
    
    List<User> findBySellerVerified(Boolean verified);
}
