package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Organizer;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
}
