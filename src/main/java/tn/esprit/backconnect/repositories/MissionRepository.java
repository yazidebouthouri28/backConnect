package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Mission;
import tn.esprit.backconnect.entities.MissionType;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByIsActiveTrue();

    List<Mission> findByMissionType(MissionType missionType);
}
