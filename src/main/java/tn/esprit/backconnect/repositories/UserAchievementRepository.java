package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.UserAchievement;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByParticipantUserId(Long participantId);

    List<UserAchievement> findByAchievementId(Long achievementId);
}
