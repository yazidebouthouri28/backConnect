package com.camping.projet.repository;

import com.camping.projet.entity.AlerteUrgence;
import com.camping.projet.enums.StatutAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlerteUrgenceRepository extends JpaRepository<AlerteUrgence, Long> {

    List<AlerteUrgence> findByStatut(StatutAlerte statut);

    @Query("SELECT a FROM AlerteUrgence a WHERE a.statut IN (com.camping.projet.enums.StatutAlerte.EN_COURS, com.camping.projet.enums.StatutAlerte.EN_INTERVENTION)")
    List<AlerteUrgence> findActiveAlerts();

    @Query("SELECT a FROM AlerteUrgence a WHERE a.declencheurId = :userId")
    List<AlerteUrgence> findByDeclencheur(@Param("userId") Long userId);

    @Query("SELECT a FROM AlerteUrgence a WHERE a.nbBlesses > 0")
    List<AlerteUrgence> findAlertsWithInjured();
}
