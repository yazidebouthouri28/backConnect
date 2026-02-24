package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.UserMission;
import tn.esprit.backconnect.services.IUserMissionService;

import java.util.List;

@RestController
@RequestMapping("/api/user-missions")
@RequiredArgsConstructor
@Tag(name = "User Missions", description = "User mission progress tracking APIs")
public class UserMissionController {

    private final IUserMissionService userMissionService;

    @GetMapping
    @Operation(summary = "Get all user missions")
    public ResponseEntity<List<UserMission>> getAllUserMissions() {
        return ResponseEntity.ok(userMissionService.getAllUserMissions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user mission by ID")
    public ResponseEntity<UserMission> getUserMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(userMissionService.getUserMissionById(id));
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign a mission to a participant")
    public ResponseEntity<UserMission> assignMission(@RequestParam Long participantId, @RequestParam Long missionId) {
        return new ResponseEntity<>(userMissionService.assignMissionToParticipant(participantId, missionId),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}/progress")
    @Operation(summary = "Update mission progress")
    public ResponseEntity<UserMission> updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        return ResponseEntity.ok(userMissionService.updateProgress(id, progress));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark mission as completed")
    public ResponseEntity<UserMission> completeMission(@PathVariable Long id) {
        return ResponseEntity.ok(userMissionService.completeMission(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user mission")
    public ResponseEntity<Void> deleteUserMission(@PathVariable Long id) {
        userMissionService.deleteUserMission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get all missions for a participant")
    public ResponseEntity<List<UserMission>> getUserMissionsByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(userMissionService.getUserMissionsByParticipant(participantId));
    }

    @GetMapping("/participant/{participantId}/completed")
    @Operation(summary = "Get completed missions for a participant")
    public ResponseEntity<List<UserMission>> getCompletedMissions(@PathVariable Long participantId) {
        return ResponseEntity.ok(userMissionService.getCompletedMissions(participantId));
    }

    @GetMapping("/participant/{participantId}/in-progress")
    @Operation(summary = "Get in-progress missions for a participant")
    public ResponseEntity<List<UserMission>> getInProgressMissions(@PathVariable Long participantId) {
        return ResponseEntity.ok(userMissionService.getInProgressMissions(participantId));
    }
}
