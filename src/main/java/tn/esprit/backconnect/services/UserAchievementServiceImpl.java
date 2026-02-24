package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Achievement;
import tn.esprit.backconnect.entities.Participant;
import tn.esprit.backconnect.entities.UserAchievement;
import tn.esprit.backconnect.repositories.AchievementRepository;
import tn.esprit.backconnect.repositories.UserAchievementRepository;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements IUserAchievementService {

    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final EntityManager entityManager;

    @Override
    public List<UserAchievement> getAllUserAchievements() {
        return userAchievementRepository.findAll();
    }

    @Override
    public UserAchievement getUserAchievementById(Long id) {
        return userAchievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAchievement not found with id: " + id));
    }

    @Override
    public UserAchievement unlockAchievement(Long participantId, Long achievementId) {
        Participant participant = entityManager.find(Participant.class, participantId);
        if (participant == null) {
            throw new RuntimeException("Participant not found with id: " + participantId);
        }
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found with id: " + achievementId));

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setParticipant(participant);
        userAchievement.setAchievement(achievement);
        userAchievement.setUnlockedDate(LocalDateTime.now());
        userAchievement.setIsDisplayed(true);
        return userAchievementRepository.save(userAchievement);
    }

    @Override
    public void deleteUserAchievement(Long id) {
        userAchievementRepository.deleteById(id);
    }

    @Override
    public List<UserAchievement> getUserAchievementsByParticipant(Long participantId) {
        return userAchievementRepository.findByParticipantUserId(participantId);
    }

    @Override
    public UserAchievement toggleDisplay(Long id) {
        UserAchievement userAchievement = getUserAchievementById(id);
        userAchievement.setIsDisplayed(!userAchievement.getIsDisplayed());
        return userAchievementRepository.save(userAchievement);
    }
}
