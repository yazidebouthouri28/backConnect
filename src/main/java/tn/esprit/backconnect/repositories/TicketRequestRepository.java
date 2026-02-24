package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.TicketRequest;
import tn.esprit.backconnect.entities.TicketRequestStatus;

import java.util.List;

@Repository
public interface TicketRequestRepository extends JpaRepository<TicketRequest, Long> {
    List<TicketRequest> findByParticipantUserId(Long participantId);

    List<TicketRequest> findByEventId(Long eventId);

    List<TicketRequest> findByStatus(TicketRequestStatus status);
}
