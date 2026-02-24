package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Mission;
import tn.esprit.backconnect.entities.Participant;
import tn.esprit.backconnect.entities.UserMission;
import tn.esprit.backconnect.repositories.MissionRepository;
import tn.esprit.backconnect.repositories.UserMissionRepository;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMissionServiceImpl implements IUserMissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final EntityManager entityManager;

    @Override
    public List<UserMission> getAllUserMissions() {
        return userMissionRepository.findAll();
    }

    @Override
    public UserMission getUserMissionById(Long id) {
        return userMissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserMission not found with id: " + id));
    }

    @Override
    public UserMission assignMissionToParticipant(Long participantId, Long missionId) {
        Participant participant = entityManager.find(Participant.class, participantId);
        if (participant == null) {
            throw new RuntimeException("Participant not found with id: " + participantId);
        }
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        UserMission userMission = new UserMission();
        userMission.setParticipant(participant);
        userMission.setMission(mission);
        userMission.setProgress(0);
        userMission.setIsCompleted(false);
        userMission.setStartedDate(LocalDateTime.now());
        return userMissionRepository.save(userMission);
    }

    @Override
    public UserMission updateProgress(Long id, Integer progress) {
        UserMission userMission = getUserMissionById(id);
        userMission.setProgress(progress);

        // Auto-complete if progress reaches target
        Mission mission = userMission.getMission();
        if (mission.getTargetCount() != null && progress >= mission.getTargetCount()) {
            userMission.setIsCompleted(true);
            userMission.setCompletedDate(LocalDateTime.now());

            // Award XP to participant
            Participant participant = userMission.getParticipant();
            participant.setXpPoints(participant.getXpPoints() + mission.getXpReward());
        }
        return userMissionRepository.save(userMission);
    }

    @Override
    public UserMission completeMission(Long id) {
        UserMission userMission = getUserMissionById(id);
        userMission.setIsCompleted(true);
        userMission.setCompletedDate(LocalDateTime.now());

        // Award XP
        Participant participant = userMission.getParticipant();
        Mission mission = userMission.getMission();
        participant.setXpPoints(participant.getXpPoints() + mission.getXpReward());

        return userMissionRepository.save(userMission);
    }

    @Override
    public void deleteUserMission(Long id) {
        userMissionRepository.deleteById(id);
    }

    @Override
    public List<UserMission> getUserMissionsByParticipant(Long participantId) {
        return userMissionRepository.findByParticipantUserId(participantId);
    }

    @Override
    public List<UserMission> getCompletedMissions(Long participantId) {
        return userMissionRepository.findByParticipantUserIdAndIsCompletedTrue(participantId);
    }

    @Override
    public List<UserMission> getInProgressMissions(Long participantId) {
        return userMissionRepository.findByParticipantUserIdAndIsCompletedFalse(participantId);
    }
}
