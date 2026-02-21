package com.camping.projet.repository;

import com.camping.projet.entity.InterventionSecours;
import com.camping.projet.enums.StatutIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterventionSecoursRepository extends JpaRepository<InterventionSecours, Long> {

    List<InterventionSecours> findByAlerteId(Long alerteId);

    List<InterventionSecours> findByStatut(StatutIntervention statut);

    @Query("SELECT i FROM InterventionSecours i WHERE :userId MEMBER OF i.membresEquipeIds")
    List<InterventionSecours> findByMembreEquipe(@Param("userId") Long userId);

    @Query("SELECT i FROM InterventionSecours i WHERE i.statut = com.camping.projet.enums.StatutIntervention.TERMINEE AND i.rapportComplet IS NULL")
    List<InterventionSecours> findPendingReports();
}
