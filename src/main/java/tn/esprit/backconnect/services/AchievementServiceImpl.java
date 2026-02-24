package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Achievement;
import tn.esprit.backconnect.entities.BadgeLevel;
import tn.esprit.backconnect.repositories.AchievementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements IAchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public Achievement getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found with id: " + id));
    }

    @Override
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public Achievement updateAchievement(Long id, Achievement achievement) {
        Achievement existing = getAchievementById(id);
        existing.setTitle(achievement.getTitle());
        existing.setDescription(achievement.getDescription());
        existing.setIconUrl(achievement.getIconUrl());
        existing.setRequiredXp(achievement.getRequiredXp());
        existing.setBadgeLevel(achievement.getBadgeLevel());
        return achievementRepository.save(existing);
    }

    @Override
    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }

    @Override
    public List<Achievement> getAchievementsByBadgeLevel(BadgeLevel badgeLevel) {
        return achievementRepository.findByBadgeLevel(badgeLevel);
    }
}
