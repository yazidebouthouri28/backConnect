package com.camping.projet.repository;

import com.camping.projet.entity.CandidatureService;
import com.camping.projet.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatureServiceRepository extends JpaRepository<CandidatureService, Long> {

    List<CandidatureService> findByUserId(Long userId);

    List<CandidatureService> findByEventServiceId(Long eventServiceId);

    Optional<CandidatureService> findByUserIdAndEventServiceId(Long userId, Long eventServiceId);

    @Query("SELECT c FROM CandidatureService c WHERE c.eventService.eventId = :eventId")
    List<CandidatureService> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT c FROM CandidatureService c WHERE c.status = :status AND c.eventService.id = :eventServiceId")
    List<CandidatureService> findByStatusAndEventService(@Param("status") ApplicationStatus status,
            @Param("eventServiceId") Long eventServiceId);
}
