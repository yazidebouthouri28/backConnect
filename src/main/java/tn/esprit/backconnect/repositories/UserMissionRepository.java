package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.UserMission;

import java.util.List;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    List<UserMission> findByParticipantUserId(Long participantId);

    List<UserMission> findByMissionId(Long missionId);

    List<UserMission> findByParticipantUserIdAndIsCompletedFalse(Long participantId);

    List<UserMission> findByParticipantUserIdAndIsCompletedTrue(Long participantId);
}
