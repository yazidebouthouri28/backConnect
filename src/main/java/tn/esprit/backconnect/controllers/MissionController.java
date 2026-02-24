package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Mission;
import tn.esprit.backconnect.services.IMissionService;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Gamification mission management APIs")
public class MissionController {

    private final IMissionService missionService;

    @GetMapping
    @Operation(summary = "Get all missions")
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mission by ID")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new mission")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        return new ResponseEntity<>(missionService.createMission(mission), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a mission")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        return ResponseEntity.ok(missionService.updateMission(id, mission));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mission")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active missions")
    public ResponseEntity<List<Mission>> getActiveMissions() {
        return ResponseEntity.ok(missionService.getActiveMissions());
    }
}
