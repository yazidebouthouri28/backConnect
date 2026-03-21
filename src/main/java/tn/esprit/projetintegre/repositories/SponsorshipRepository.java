package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.Sponsorship;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {
    
    List<Sponsorship> findBySponsor_Id(Long sponsorId);
    
    List<Sponsorship> findByEvent_Id(Long eventId);
    
    List<Sponsorship> findByStatus(String status);
    
    List<Sponsorship> findByIsActiveTrue();
    
    List<Sponsorship> findByIsPaidFalse();
    
    @Query("SELECT s FROM Sponsorship s WHERE s.startDate <= :date AND s.endDate >= :date AND s.isActive = true")
    List<Sponsorship> findActiveOnDate(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(s.amount) FROM Sponsorship s WHERE s.event.id = :eventId AND s.isPaid = true")
    java.math.BigDecimal getTotalPaidAmountByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT s FROM Sponsorship s LEFT JOIN FETCH s.sponsor LEFT JOIN FETCH s.event WHERE s.id = :id")
    java.util.Optional<Sponsorship> findByIdWithDetails(@Param("id") Long id);
}
