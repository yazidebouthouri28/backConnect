package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.MessageReaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    List<MessageReaction> findByMessageId(Long messageId);
    List<MessageReaction> findByUserId(Long userId);
    Optional<MessageReaction> findByMessageIdAndUserId(Long messageId, Long userId);
    void deleteByMessageIdAndUserId(Long messageId, Long userId);
    long countByMessageId(Long messageId);
}
