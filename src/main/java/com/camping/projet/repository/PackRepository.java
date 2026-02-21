package com.camping.projet.repository;

import com.camping.projet.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {

    @Query("SELECT p FROM Pack p WHERE p.active = true AND :date BETWEEN p.startDate AND p.endDate")
    List<Pack> findActivePacks(@Param("date") LocalDate date);

    @Query("SELECT p FROM Pack p JOIN p.services s WHERE s.id = :serviceId")
    List<Pack> findPacksByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT p FROM Pack p WHERE p.savingsPercentage >= :minSavings")
    List<Pack> findPacksWithSavings(@Param("minSavings") Double minSavings);
}
