package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.EmailNotification;
import tn.esprit.projetPi.entities.EmailType;

import java.util.List;

@Repository
public interface EmailNotificationRepository extends MongoRepository<EmailNotification, String> {
    
    List<EmailNotification> findByUserId(String userId);
    
    List<EmailNotification> findByToEmail(String toEmail);
    
    List<EmailNotification> findBySent(Boolean sent);
    
    Page<EmailNotification> findBySent(Boolean sent, Pageable pageable);
    
    List<EmailNotification> findByType(EmailType type);
    
    List<EmailNotification> findBySentAndRetryCountLessThan(Boolean sent, Integer maxRetries);
}
