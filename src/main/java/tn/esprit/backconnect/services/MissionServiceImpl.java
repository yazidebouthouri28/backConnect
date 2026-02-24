package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Mission;
import tn.esprit.backconnect.repositories.MissionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements IMissionService {

    private final MissionRepository missionRepository;

    @Override
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    @Override
    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + id));
    }

    @Override
    public Mission createMission(Mission mission) {
        if (mission.getIsActive() == null) {
            mission.setIsActive(true);
        }
        return missionRepository.save(mission);
    }

    @Override
    public Mission updateMission(Long id, Mission mission) {
        Mission existing = getMissionById(id);
        existing.setTitle(mission.getTitle());
        existing.setDescription(mission.getDescription());
        existing.setXpReward(mission.getXpReward());
        existing.setMissionType(mission.getMissionType());
        existing.setTargetCount(mission.getTargetCount());
        existing.setIsActive(mission.getIsActive());
        return missionRepository.save(existing);
    }

    @Override
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    @Override
    public List<Mission> getActiveMissions() {
        return missionRepository.findByIsActiveTrue();
    }
}
