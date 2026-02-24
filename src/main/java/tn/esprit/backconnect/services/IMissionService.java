package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Mission;

import java.util.List;

public interface IMissionService {
    List<Mission> getAllMissions();

    Mission getMissionById(Long id);

    Mission createMission(Mission mission);

    Mission updateMission(Long id, Mission mission);

    void deleteMission(Long id);

    List<Mission> getActiveMissions();
}
