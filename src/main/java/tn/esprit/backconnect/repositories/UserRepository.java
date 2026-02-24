package tn.esprit.backconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Role;
import tn.esprit.backconnect.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

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

    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByEmailContainingIgnoreCase(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(String query);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(String query, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countNewUsersAfter(LocalDateTime date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :date")
    long countActiveUsersAfter(LocalDateTime date);

    long countByRole(Role role);

    long countByIsSeller(Boolean isSeller);

    long countByIsActive(Boolean isActive);

    long countByIsSuspended(Boolean isSuspended);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    List<User> findBySellerVerified(Boolean verified);
}
