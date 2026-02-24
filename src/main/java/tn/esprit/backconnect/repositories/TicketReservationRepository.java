package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.TicketReservation;
import tn.esprit.backconnect.entities.ReservationStatus;

import java.util.List;

@Repository
public interface TicketReservationRepository extends JpaRepository<TicketReservation, Long> {
    List<TicketReservation> findByParticipantUserId(Long participantId);

    List<TicketReservation> findByEventId(Long eventId);

    List<TicketReservation> findByStatus(ReservationStatus status);
}
