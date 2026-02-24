package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.UserMission;

import java.util.List;

public interface IUserMissionService {
    List<UserMission> getAllUserMissions();

    UserMission getUserMissionById(Long id);

    UserMission assignMissionToParticipant(Long participantId, Long missionId);

    UserMission updateProgress(Long id, Integer progress);

    UserMission completeMission(Long id);

    void deleteUserMission(Long id);

    List<UserMission> getUserMissionsByParticipant(Long participantId);

    List<UserMission> getCompletedMissions(Long participantId);

    List<UserMission> getInProgressMissions(Long participantId);
}
