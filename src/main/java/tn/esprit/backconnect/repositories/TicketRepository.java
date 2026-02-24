package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Ticket;
import tn.esprit.backconnect.entities.TicketStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByReservationTicketReservationId(Long reservationId);

    List<Ticket> findByStatus(TicketStatus status);

    Optional<Ticket> findByTicketNumber(String ticketNumber);
}
