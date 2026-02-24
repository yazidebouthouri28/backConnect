package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.UserAchievement;
import tn.esprit.backconnect.services.IUserAchievementService;

import java.util.List;

@RestController
@RequestMapping("/api/user-achievements")
@RequiredArgsConstructor
@Tag(name = "User Achievements", description = "User achievement tracking APIs")
public class UserAchievementController {

    private final IUserAchievementService userAchievementService;

    @GetMapping
    @Operation(summary = "Get all user achievements")
    public ResponseEntity<List<UserAchievement>> getAllUserAchievements() {
        return ResponseEntity.ok(userAchievementService.getAllUserAchievements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user achievement by ID")
    public ResponseEntity<UserAchievement> getUserAchievementById(@PathVariable Long id) {
        return ResponseEntity.ok(userAchievementService.getUserAchievementById(id));
    }

    @PostMapping("/unlock")
    @Operation(summary = "Unlock an achievement for a participant")
    public ResponseEntity<UserAchievement> unlockAchievement(@RequestParam Long participantId,
            @RequestParam Long achievementId) {
        return new ResponseEntity<>(userAchievementService.unlockAchievement(participantId, achievementId),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user achievement")
    public ResponseEntity<Void> deleteUserAchievement(@PathVariable Long id) {
        userAchievementService.deleteUserAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get all achievements for a participant")
    public ResponseEntity<List<UserAchievement>> getUserAchievementsByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(userAchievementService.getUserAchievementsByParticipant(participantId));
    }

    @PutMapping("/{id}/toggle-display")
    @Operation(summary = "Toggle achievement display on profile")
    public ResponseEntity<UserAchievement> toggleDisplay(@PathVariable Long id) {
        return ResponseEntity.ok(userAchievementService.toggleDisplay(id));
    }
}
