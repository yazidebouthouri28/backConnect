package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Complaint;
import tn.esprit.backconnect.entities.ComplaintStatus;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByEventId(Long eventId);

    List<Complaint> findByParticipantUserId(Long participantId);

    List<Complaint> findByStatus(ComplaintStatus status);
}
