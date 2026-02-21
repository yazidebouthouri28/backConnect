package com.camping.projet.repository;

import com.camping.projet.entity.User;
import com.camping.projet.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetToken(String token);

    List<User> findByRole(Role role);

    List<User> findByCampingId(Long campingId);

    @Query("SELECT u FROM User u WHERE u.actif = true AND u.emailVerified = true")
    List<User> findAllActiveAndVerified();

    @Query("SELECT u FROM User u WHERE u.campingId = :campingId AND u.role = :role")
    List<User> findStaffByCampingAndRole(@Param("campingId") Long campingId, @Param("role") Role role);
}
