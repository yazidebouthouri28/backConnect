package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
