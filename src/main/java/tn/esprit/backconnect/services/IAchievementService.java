package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Achievement;
import tn.esprit.backconnect.entities.BadgeLevel;

import java.util.List;

public interface IAchievementService {
    List<Achievement> getAllAchievements();

    Achievement getAchievementById(Long id);

    Achievement createAchievement(Achievement achievement);

    Achievement updateAchievement(Long id, Achievement achievement);

    void deleteAchievement(Long id);

    List<Achievement> getAchievementsByBadgeLevel(BadgeLevel badgeLevel);
}
