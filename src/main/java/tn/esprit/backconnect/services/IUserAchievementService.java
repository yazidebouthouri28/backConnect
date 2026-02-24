package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.UserAchievement;

import java.util.List;

public interface IUserAchievementService {
    List<UserAchievement> getAllUserAchievements();

    UserAchievement getUserAchievementById(Long id);

    UserAchievement unlockAchievement(Long participantId, Long achievementId);

    void deleteUserAchievement(Long id);

    List<UserAchievement> getUserAchievementsByParticipant(Long participantId);

    UserAchievement toggleDisplay(Long id);
}
