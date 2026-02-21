package com.camping.projet.repository;

import com.camping.projet.entity.ExerciceEvacuation;
import com.camping.projet.enums.TypeExercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciceEvacuationRepository extends JpaRepository<ExerciceEvacuation, Long> {

    List<ExerciceEvacuation> findByType(TypeExercice type);

    @Query("SELECT e FROM ExerciceEvacuation e WHERE e.responsableId = :userId")
    List<ExerciceEvacuation> findByResponsable(@Param("userId") Long userId);

    @Query("SELECT e FROM ExerciceEvacuation e WHERE e.reussi = false")
    List<ExerciceEvacuation> findFailedExercises();

    @Query("SELECT e FROM ExerciceEvacuation e WHERE e.dateHeure > :now ORDER BY e.dateHeure ASC")
    List<ExerciceEvacuation> findUpcomingExercises(@Param("now") LocalDateTime now);
}
