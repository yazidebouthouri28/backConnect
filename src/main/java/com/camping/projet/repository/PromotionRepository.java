package com.camping.projet.repository;

import com.camping.projet.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodePromo(String codePromo);

    @Query("SELECT p FROM Promotion p WHERE p.actif = true AND :now BETWEEN p.dateDebut AND p.dateFin")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.nbUtilisationsActuelles < p.maxUtilisations OR p.maxUtilisations IS NULL")
    List<Promotion> findPromotionsWithRemainingUses();
}
