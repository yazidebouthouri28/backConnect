package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Event;
import tn.esprit.backconnect.entities.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);

    List<Event> findByOrganizerUserId(Long organizerId);

    List<Event> findByEventTypeId(Long eventTypeId);

    List<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByLocationContainingIgnoreCase(String location);
}
