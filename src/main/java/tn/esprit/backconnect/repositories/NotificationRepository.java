package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserId(Long recipientId);

    List<Notification> findByEventId(Long eventId);

    List<Notification> findByIsReadFalseAndRecipientUserId(Long recipientId);
}
