package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.EventType;

import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    Optional<EventType> findByNameIgnoreCase(String name);
}
