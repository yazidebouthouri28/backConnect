package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.Points;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {

    @Query("SELECT SUM(p.points) FROM Points p WHERE p.user.id = :userId AND p.expired = false AND p.used = false")
    Integer getAvailablePoints(@Param("userId") Long userId);

    @Query("SELECT p FROM Points p WHERE p.expirationDate <= :date AND p.expired = false")
    List<Points> findExpiringPoints(@Param("date") LocalDateTime date);

    // Version avec méthode dérivée (sans @Query)
    List<Points> findByUserIdAndExpiredFalseAndUsedFalseAndExpirationDateIsNullOrExpirationDateAfterOrderByExpirationDateAsc(
            Long userId, LocalDateTime now);

    // Ou version avec @Query plus simple
    @Query("SELECT p FROM Points p WHERE p.user.id = :userId AND p.expired = false AND p.used = false AND (p.expirationDate IS NULL OR p.expirationDate > :now) ORDER BY p.expirationDate ASC")
    List<Points> findAvailablePointsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}