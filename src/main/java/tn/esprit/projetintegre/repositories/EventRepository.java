package tn.esprit.projetintegre.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.Event;
import tn.esprit.projetintegre.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);
    Page<Event> findBySiteId(Long siteId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate > :now ORDER BY e.startDate")
    List<Event> findUpcomingEvents(LocalDateTime now, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Event> searchEvents(String keyword, Pageable pageable);
    
    List<Event> findByIsPublicTrue();
}
