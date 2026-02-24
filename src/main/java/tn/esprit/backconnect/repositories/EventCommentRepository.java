package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.EventComment;

import java.util.List;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEventId(Long eventId);

    List<EventComment> findByParticipantUserId(Long participantId);
}
