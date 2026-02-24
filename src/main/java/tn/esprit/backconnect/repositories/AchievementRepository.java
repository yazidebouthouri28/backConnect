package tn.esprit.backconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.backconnect.entities.Achievement;
import tn.esprit.backconnect.entities.BadgeLevel;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByBadgeLevel(BadgeLevel badgeLevel);

    List<Achievement> findByRequiredXpLessThanEqual(Integer xp);
}
