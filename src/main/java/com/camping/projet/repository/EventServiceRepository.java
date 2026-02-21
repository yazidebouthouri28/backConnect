package com.camping.projet.repository;

import com.camping.projet.entity.EventService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventServiceRepository extends JpaRepository<EventService, Long> {

    List<EventService> findByEventId(Long eventId);

    @Query("SELECT es FROM EventService es JOIN FETCH es.service WHERE es.eventId = :eventId")
    List<EventService> findByEventIdWithService(@Param("eventId") Long eventId);

    Optional<EventService> findByEventIdAndServiceId(Long eventId, Long serviceId);

    @Query("SELECT es FROM EventService es WHERE es.acceptedQuantity < es.requiredQuantity")
    List<EventService> findAllWithAvailableSpots();
}
